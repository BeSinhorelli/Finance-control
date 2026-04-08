package com.finance.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate transactionDate;
    private Long userId;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}