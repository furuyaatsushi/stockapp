package com.example.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockTradeDto {

    private Long id;
    private String stockCode;
    private String stockName;
    private String tradeType; // BUY / SELL
    private int quantity;
    private BigDecimal price;
    private LocalDate tradeDate;

    public StockTradeDto(
            Long id,
            String stockCode,
            String stockName,
            String tradeType,
            int quantity,
            BigDecimal price,
            LocalDate tradeDate) {
        this.id = id;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.tradeDate = tradeDate;
    }

    // --- getter ---

    public Long getId() { return id; }
    public String getStockCode() { return stockCode; }
    public String getStockName() { return stockName; }
    public String getTradeType() { return tradeType; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public LocalDate getTradeDate() { return tradeDate; }
}

