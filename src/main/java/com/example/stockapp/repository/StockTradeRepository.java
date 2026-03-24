package com.example.stockapp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.stockapp.entity.AccountType;
import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.TradeType;
import com.example.stockapp.entity.User;

@Repository
public interface StockTradeRepository
        extends JpaRepository<StockTrade, Long> {

    // ログインユーザーの取引一覧
    List<StockTrade> findByUser_Id(Long userId);

    List<StockTrade> findByUser(User user);

    List<StockTrade> findByStockId(Long stockId);

    // ユーザー + 銘柄ごとの取引
    List<StockTrade> findByUser_IdAndStock_Id(Long userId, Long stockId);

    List<StockTrade> findByUserOrderByTradeDateDesc(User user);
    List<StockTrade> findByUserOrderByTradeDateAsc(User user);

    List<StockTrade> findByUserAndStock_StockCode(
            User user,
            String stockCode
    );

    List<StockTrade> findByUserAndTradeType(User user, TradeType tradeType);

    List<StockTrade> findByUserAndStock_StockCodeAndAccountTypeOrderByTradeDateAsc(
            User user,
            String stockCode,
            AccountType accountType
    );

    Optional<StockTrade> findTopByUserAndStockOrderByTradeDateAsc(
            User user,
            Stock stock
    );

    List<StockTrade> findByUserAndStockId(User user, Long stockId);

    //現在保有数取得
    @Query("""
    SELECT SUM(
        CASE 
            WHEN t.tradeType = 'BUY' THEN t.quantity
            ELSE -t.quantity
        END
    )
    FROM StockTrade t
    WHERE t.user = :user
    AND t.stock = :stock
    AND t.accountType = :accountType
    """)

    Integer calculateHoldingQuantity(
            @Param("user") User user,
            @Param("stock") Stock stock,
            @Param("accountType") AccountType accountType
    );

    @Query("""
        SELECT COALESCE(SUM(s.realizedProfit), 0)
        FROM StockTrade s
        WHERE s.user = :user
        AND s.tradeType = 'SELL'
        """)
    BigDecimal sumRealizedProfitByUser(User user);

}
