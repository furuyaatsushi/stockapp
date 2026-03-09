package com.example.stockapp.dto;

import com.example.stockapp.entity.AccountType;

import java.math.BigDecimal;

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

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public BigDecimal getTotalBuyAmount(){
        return totalBuyAmount;
    }

    public BigDecimal getHoldingAmount(){
        return holdingAmount;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
