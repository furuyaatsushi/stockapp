package com.example.stockapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false, unique = true, length = 10)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    // --- getter / setter ---

    public Long getId() {
        return id;
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
}
