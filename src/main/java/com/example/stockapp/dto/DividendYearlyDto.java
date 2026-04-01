package com.example.stockapp.dto;

import java.math.BigDecimal;

public class DividendYearlyDto {

    private int year;
    private BigDecimal totalDividend;
    private BigDecimal totalInvestment;
    private BigDecimal yield;


    public int getYear(){
        return year;
    }

    public void setYear(int year){
       this.year = year; 
    }

    public BigDecimal getTotalDividend(){
        return totalDividend;
    }

    public void setTotalDividend(BigDecimal totalDividend){
        this.totalDividend = totalDividend;
    }

    public BigDecimal getTotalInvestment(){
        return totalInvestment;
    }

    public void setTotalInvestment(BigDecimal totalInvestment){
        this.totalInvestment = totalInvestment;
    }

    public BigDecimal getYield(){
        return yield;
    }

    public void setYield(BigDecimal yield){
        this.yield = yield;
    }
    
}
