package com.example.stockapp.dto;

import java.math.BigDecimal;

public class StockHoldingDto {

    private String stockCode;
    private String stockName;
    private int quantity;
    private BigDecimal averagePrice;

    public StockHoldingDto(
            String stockCode,
            String stockName,
            int quantity,
            BigDecimal averagePrice) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
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
}
