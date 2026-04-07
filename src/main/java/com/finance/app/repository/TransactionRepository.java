package com.finance.app.repository;

import com.finance.app.model.Transaction;
import com.finance.app.model.TransactionType;
import com.finance.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(User user, LocalDate start, LocalDate end);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByUserAndType(@Param("user") User user, @Param("type") TransactionType type, 
                                 @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user = :user AND t.type = 'EXPENSE' AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getExpensesByCategory(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);
}