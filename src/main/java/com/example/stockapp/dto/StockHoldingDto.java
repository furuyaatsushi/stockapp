package com.example.stockapp.dto;

import java.math.BigDecimal;

import com.example.stockapp.entity.AccountType;

public class StockHoldingDto {

    private Long stockId;
    private String stockCode;
    private String stockName;
    private int quantity;
    private BigDecimal averagePrice;
    private BigDecimal totalBuyAmount;
    private BigDecimal holdingAmount;
    private AccountType accountType;

    public StockHoldingDto(
            Long stockId,
            String stockCode,
            String stockName,
            int quantity,
            BigDecimal averagePrice,
            BigDecimal totalBuyAmount,
            BigDecimal holdingAmount,
            AccountType accountType) {
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.totalBuyAmount = totalBuyAmount;
        this.holdingAmount = holdingAmount;
        this.accountType = accountType;
    }

    public StockHoldingDto() {
    }

public Long getStockId() {
    return stockId;
}

public void setStockId(Long stockId) {
    this.stockId = stockId;
}

public String getStockCode() {
    return stockCode;
}

public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
}

public String getStockName() {
    return stockName;
}

public void setStockName(String stockName) {
    this.stockName = stockName;
}

public int getQuantity() {
    return quantity;
}

public void setQuantity(int quantity) {
    this.quantity = quantity;
}

public BigDecimal getAveragePrice() {
    return averagePrice;
}

public void setAveragePrice(BigDecimal averagePrice) {
    this.averagePrice = averagePrice;
}

public BigDecimal getTotalBuyAmount() {
    return totalBuyAmount;
}

public void setTotalBuyAmount(BigDecimal totalBuyAmount) {
    this.totalBuyAmount = totalBuyAmount;
}

public BigDecimal getHoldingAmount() {
    return holdingAmount;
}

public void setHoldingAmount(BigDecimal holdingAmount) {
    this.holdingAmount = holdingAmount;
}

public AccountType getAccountType() {
    return accountType;
}

public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
}
}
