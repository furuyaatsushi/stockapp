package com.example.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.stockapp.entity.AccountType;

public class DividendRequest {

    private Long stockId;

    private AccountType accountType;

    private BigDecimal amount;

    private LocalDate dividendDate;

    // getter setter

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDividendDate() {
        return dividendDate;
    }

    public void setDividendDate(LocalDate dividendDate) {
        this.dividendDate = dividendDate;
    }
}