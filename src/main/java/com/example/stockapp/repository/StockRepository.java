package com.example.stockapp.repository;

import com.example.stockapp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository
        extends JpaRepository<Stock, Long> {

    Optional<Stock> findByStockCode(String stockCode);
}

