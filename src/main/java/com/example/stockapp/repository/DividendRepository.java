package com.example.stockapp.repository;

import com.example.stockapp.entity.Dividend;
import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    // ユーザーの配当履歴
    List<Dividend> findByUserOrderByDividendDateDesc(User user);

    // 銘柄ごとの配当履歴
    List<Dividend> findByUserAndStockOrderByDividendDateDesc(User user, Stock stock);
}
