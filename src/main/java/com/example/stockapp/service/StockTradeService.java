package com.example.stockapp.service;

import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.User;
import com.example.stockapp.dto.StockHoldingDto;
import com.example.stockapp.dto.StockTradeDto;
import com.example.stockapp.repository.StockRepository;
import com.example.stockapp.repository.StockTradeRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockTradeService {

    private final StockTradeRepository stockTradeRepository;
    private final StockRepository stockRepository;

    public StockTradeService(
            StockTradeRepository stockTradeRepository,
            StockRepository stockRepository) {
        this.stockTradeRepository = stockTradeRepository;
        this.stockRepository = stockRepository;
    }

    public List<StockTrade> getTradesByUser(User user) {
        return stockTradeRepository.findByUserOrderByTradeDateDesc(user);
    }

    public List<StockTradeDto> getTradeDtosByUser(User user) {
        return stockTradeRepository.findByUserOrderByTradeDateDesc(user)
            .stream()
            .map(trade -> new StockTradeDto(
                    trade.getId(),
                    trade.getStock().getStockCode(),
                    trade.getStock().getStockName(),
                    trade.getTradeType(),
                    trade.getQuantity(),
                    trade.getPrice(),
                    trade.getTradeDate()
            ))
            .toList();
    }

    public List<StockHoldingDto> getCurrentHoldings(User user) {
        List<StockTrade> trades = stockTradeRepository.findByUser(user);

        // 銘柄コードごとにまとめる
        Map<String, List<StockTrade>> byStock =
            trades.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getStock().getStockCode()
                    ));

        List<StockHoldingDto> result = new ArrayList<>();

        for (List<StockTrade> stockTrades : byStock.values()) {

            String stockCode = stockTrades.get(0).getStock().getStockCode();
            String stockName = stockTrades.get(0).getStock().getStockName();

            int totalQuantity = 0;
            BigDecimal totalBuyAmount = BigDecimal.ZERO;

            for (StockTrade trade : stockTrades) {
                if ("BUY".equals(trade.getTradeType())) {
                    totalQuantity += trade.getQuantity();
                    totalBuyAmount = totalBuyAmount.add(
                            trade.getPrice()
                                .multiply(BigDecimal.valueOf(trade.getQuantity()))
                    );
                } else if ("SELL".equals(trade.getTradeType())) {
                    totalQuantity -= trade.getQuantity();
                }
            }

            // 売り切っている銘柄は除外
            if (totalQuantity <= 0) {
                continue;
            }

            BigDecimal averagePrice =
                    totalBuyAmount.divide(
                            BigDecimal.valueOf(totalQuantity),
                            2,
                            RoundingMode.HALF_UP
                    );

            result.add(new StockHoldingDto(
                    stockCode,
                    stockName,
                    totalQuantity,
                    averagePrice
            ));

        }

        return result;
    }

    @Transactional
    public void buyStock(
            User user,
            String stockCode,
            String stockName,
            int quantity,
            BigDecimal price,
            LocalDate tradeDate) {

        // 銘柄がなければ作成
        Stock stock = stockRepository
                .findByStockCode(stockCode)
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setStockCode(stockCode);
                    s.setStockName(stockName);
                    return stockRepository.save(s);
                });

        StockTrade trade = new StockTrade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setTradeType("BUY");
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTradeDate(tradeDate);

        stockTradeRepository.save(trade);
    }

    @Transactional
    public void sellStock(
            User user,
            String stockCode,
            int quantity,
            BigDecimal price,
            LocalDate tradeDate) {

        Stock stock = stockRepository
                .findByStockCode(stockCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("銘柄が存在しません"));

        StockTrade trade = new StockTrade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setTradeType("SELL");
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTradeDate(tradeDate);

        stockTradeRepository.save(trade);
    }
}

