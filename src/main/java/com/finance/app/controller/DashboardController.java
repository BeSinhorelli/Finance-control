package com.finance.app.controller;

import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import com.finance.app.repository.TransactionRepository;
import com.finance.app.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardController(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary(@RequestParam Long userId,
                                           @RequestParam String startDate,
                                           @RequestParam String endDate) {
        Map<String, Object> response = new HashMap<>();
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("error", "Usuário não encontrado");
            return response;
        }
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        BigDecimal totalIncomes = transactionRepository.sumByUserAndType(user, TransactionType.INCOME, start, end);
        BigDecimal totalExpenses = transactionRepository.sumByUserAndType(user, TransactionType.EXPENSE, start, end);
        BigDecimal balance = totalIncomes.subtract(totalExpenses);
        
        List<Object[]> expensesByCategory = transactionRepository.getExpensesByCategory(user, start, end);
        
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        for (Object[] row : expensesByCategory) {
            categoryMap.put((String) row[0], (BigDecimal) row[1]);
        }
        
        response.put("totalIncomes", totalIncomes);
        response.put("totalExpenses", totalExpenses);
        response.put("balance", balance);
        response.put("expensesByCategory", categoryMap);
        
        return response;
    }
}