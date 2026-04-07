package com.finance.app.service;

import com.finance.app.model.Category;
import com.finance.app.model.Transaction;
import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import com.finance.app.repository.CategoryRepository;
import com.finance.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    
    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public Transaction create(String description, BigDecimal amount, String type, LocalDate date, Long categoryId, User user) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.valueOf(type));
        transaction.setTransactionDate(date);
        transaction.setCategory(category);
        transaction.setUser(user);
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction update(Long id, String description, BigDecimal amount, String type, LocalDate date, Long categoryId, User user) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.valueOf(type));
        transaction.setTransactionDate(date);
        transaction.setCategory(category);
        
        return transactionRepository.save(transaction);
    }
    
    public void delete(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }
        
        transactionRepository.delete(transaction);
    }
    
    public List<Transaction> findByPeriod(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(user, start, end);
    }
    
    public BigDecimal getTotalIncomes(User user, LocalDate start, LocalDate end) {
        return transactionRepository.sumByUserAndType(user, TransactionType.INCOME, start, end);
    }
    
    public BigDecimal getTotalExpenses(User user, LocalDate start, LocalDate end) {
        return transactionRepository.sumByUserAndType(user, TransactionType.EXPENSE, start, end);
    }
    
    public List<Object[]> getExpensesByCategory(User user, LocalDate start, LocalDate end) {
        return transactionRepository.getExpensesByCategory(user, start, end);
    }
}