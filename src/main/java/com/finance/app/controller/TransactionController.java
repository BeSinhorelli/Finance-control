package com.finance.app.controller;

import com.finance.app.dto.TransactionDTO;
import com.finance.app.model.Transaction;
import com.finance.app.model.User;
import com.finance.app.repository.UserRepository;
import com.finance.app.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    
    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }
    
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody TransactionDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = getUser(dto.getId() != null ? dto.getId() : 1L);
            Transaction transaction = transactionService.create(
                dto.getDescription(), dto.getAmount(), dto.getType(),
                dto.getTransactionDate(), dto.getCategoryId(), user
            );
            response.put("success", true);
            response.put("transaction", transaction);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }
    
    @GetMapping
    public List<TransactionDTO> findByPeriod(@RequestParam Long userId,
                                              @RequestParam String startDate,
                                              @RequestParam String endDate) {
        User user = getUser(userId);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        return transactionService.findByPeriod(user, start, end).stream().map(t -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(t.getId());
            dto.setDescription(t.getDescription());
            dto.setAmount(t.getAmount());
            dto.setType(t.getType().toString());
            dto.setTransactionDate(t.getTransactionDate());
            dto.setCategoryName(t.getCategory().getName());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @GetMapping("/summary")
    public Map<String, Object> getSummary(@RequestParam Long userId,
                                           @RequestParam String startDate,
                                           @RequestParam String endDate) {
        User user = getUser(userId);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        BigDecimal totalIncomes = transactionService.getTotalIncomes(user, start, end);
        BigDecimal totalExpenses = transactionService.getTotalExpenses(user, start, end);
        BigDecimal balance = totalIncomes.subtract(totalExpenses);
        
        List<Object[]> categoryExpenses = transactionService.getExpensesByCategory(user, start, end);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncomes", totalIncomes);
        summary.put("totalExpenses", totalExpenses);
        summary.put("balance", balance);
        summary.put("categoryExpenses", categoryExpenses);
        
        return summary;
    }
}