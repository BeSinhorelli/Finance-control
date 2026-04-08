package com.finance.app.controller;

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

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String description = body.get("description").toString();
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String type = body.get("type").toString();
            String category = body.get("category").toString();
            LocalDate transactionDate = LocalDate.parse(body.get("transactionDate").toString());
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("error", "Usuário não encontrado");
                return response;
            }
            
            Transaction transaction = transactionService.create(description, amount, type, category, transactionDate, user);
            
            response.put("success", true);
            response.put("transaction", transaction);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping
    public List<Transaction> findAll(@RequestParam Long userId, 
                                      @RequestParam String startDate, 
                                      @RequestParam String endDate) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        return transactionService.findByPeriod(user, start, end);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            String description = body.get("description").toString();
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String type = body.get("type").toString();
            String category = body.get("category").toString();
            LocalDate transactionDate = LocalDate.parse(body.get("transactionDate").toString());
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("error", "Usuário não encontrado");
                return response;
            }
            
            Transaction transaction = transactionService.update(id, description, amount, type, category, transactionDate, user);
            
            response.put("success", true);
            response.put("transaction", transaction);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.put("error", "Usuário não encontrado");
                return response;
            }
            
            transactionService.delete(id, user);
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }
}