package com.example.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockTradeRequest {

    private String stockCode;
    private String stockName; // BUYのみ使用
    private int quantity;
    private BigDecimal price;
    private LocalDate tradeDate;

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
}
