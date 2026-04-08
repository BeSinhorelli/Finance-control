package com.finance.app.service;

import com.finance.app.model.Transaction;
import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import com.finance.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(String description, BigDecimal amount, String type, String category, LocalDate date, User user) {
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.valueOf(type));
        transaction.setCategory(category);
        transaction.setTransactionDate(date);
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    public Transaction update(Long id, String description, BigDecimal amount, String type, String category, LocalDate date, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.valueOf(type));
        transaction.setCategory(category);
        transaction.setTransactionDate(date);

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