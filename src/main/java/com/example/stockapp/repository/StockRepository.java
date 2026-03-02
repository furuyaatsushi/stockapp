package com.example.stockapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.User;

@Repository
public interface StockRepository
        extends JpaRepository<Stock, Long> {

    Optional<Stock> findByStockCode(String stockCode);

    Optional<Stock> findByUserAndStockCode(User user, String stockCode);

    Optional<Stock> findByIdAndUser_Id(Long id, Long userId);

}

