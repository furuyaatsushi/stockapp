package com.example.stockapp.repository;

import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTradeRepository
        extends JpaRepository<StockTrade, Long> {

    // ログインユーザーの取引一覧
    List<StockTrade> findByUserId(Long userId);

    List<StockTrade> findByUser(User user);

    // ユーザー + 銘柄ごとの取引
    List<StockTrade> findByUserIdAndStockId(Long userId, Long stockId);

    List<StockTrade> findByUserOrderByTradeDateDesc(User user);
}
