package com.example.stockapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.stockapp.dto.AddBuyRequest;
import com.example.stockapp.dto.NewBuyRequest;
import com.example.stockapp.dto.SellRequest;
import com.example.stockapp.dto.StockHoldingDto;
import com.example.stockapp.dto.StockTradeDto;
import com.example.stockapp.entity.AccountType;
import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.TradeType;
import com.example.stockapp.entity.User;
import com.example.stockapp.repository.StockRepository;
import com.example.stockapp.repository.StockTradeRepository;
import com.example.stockapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class StockTradeService {

    private final StockTradeRepository stockTradeRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public StockTradeService(
            StockTradeRepository stockTradeRepository,
            StockRepository stockRepository,
            UserRepository userRepository) {
        this.stockTradeRepository = stockTradeRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
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

        List<StockTrade> trades =
            stockTradeRepository.findByUserOrderByTradeDateAsc(user);

        Map<String, List<StockTrade>> byStockAccount =
                trades.stream()
                        .collect(Collectors.groupingBy(
                                t -> t.getStock().getId() + "_" + t.getAccountType()
                        ));

        List<StockHoldingDto> result = new ArrayList<>();

        for (List<StockTrade> stockTrades : byStockAccount.values()) {

            AccountType accountType = stockTrades.get(0).getAccountType();
            Stock stock = stockTrades.get(0).getStock();

            int currentQuantity = 0;
            int totalBuyQuantity = 0;

            BigDecimal totalBuyAmount = BigDecimal.ZERO;
            BigDecimal totalSellAmount = BigDecimal.ZERO;
            BigDecimal realizedProfit = BigDecimal.ZERO;

            for (StockTrade trade : stockTrades) {

                BigDecimal amount =
                    trade.getPrice()
                        .multiply(BigDecimal.valueOf(trade.getQuantity()));

                if (trade.getTradeType() == TradeType.BUY) {

                    currentQuantity += trade.getQuantity();
                    totalBuyQuantity += trade.getQuantity();
                    totalBuyAmount = totalBuyAmount.add(amount);

                } else {

                    currentQuantity -= trade.getQuantity();
                    totalSellAmount = totalSellAmount.add(amount);

                    if (trade.getRealizedProfit() != null) {
                        realizedProfit =
                            realizedProfit.add(trade.getRealizedProfit());
                    }
                }
            }

            if (currentQuantity <= 0) {
                continue;
            }

            BigDecimal averagePrice =
                totalBuyAmount.divide(
                    BigDecimal.valueOf(totalBuyQuantity),
                    0,
                    RoundingMode.DOWN
                );
            
            BigDecimal holdingAmount =
                averagePrice.multiply(BigDecimal.valueOf(currentQuantity));

            result.add(new StockHoldingDto(
                stock.getId(),
                stock.getStockCode(),
                stock.getStockName(),
                currentQuantity,
                averagePrice,
                totalBuyAmount,
                holdingAmount,
                accountType
            ));
        }

        return result;
    }

    public List<StockHoldingDto> getCurrentHoldingsByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getCurrentHoldings(user);
    }
 

    @Transactional
    public void buyStock(
            User user,
            String stockCode,
            String stockName,
            int quantity,
            BigDecimal price,
            LocalDate tradeDate) {

        System.out.println("buyStock called");
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
        trade.setTradeType(TradeType.BUY);
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTradeDate(tradeDate);

        stockTradeRepository.save(trade);
    }

    @Transactional
    public void buyNewStock(User user, NewBuyRequest request) {

        Stock stock = stockRepository
                .findByUserAndStockCode(user, request.getStockCode())
                .map(existingStock -> {

                    // 名前チェック
                    if (!existingStock.getStockName().equals(request.getStockName())) {
                        throw new IllegalArgumentException("銘柄コードに対して銘柄名が一致しません");
                    }

                    return existingStock;

                })
                .orElseGet(() -> {

                    // 新規銘柄作成
                    Stock newStock = new Stock();
                    newStock.setUser(user);
                    newStock.setStockCode(request.getStockCode());
                    newStock.setStockName(request.getStockName());
                    return stockRepository.save(newStock);

                });

        // 取引作成
        StockTrade trade = new StockTrade();
        trade.setStock(stock);
        trade.setUser(user);
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        trade.setAccountType(request.getAccountType());
        trade.setTradeDate(request.getTradeDate());
        trade.setTradeType(TradeType.BUY);

        stockTradeRepository.save(trade);
    }

    @Transactional
    public void buyAdditionalStock(User user, AddBuyRequest request) {
 
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("銘柄が存在しません"));

        // 所有者チェック
        if (!stock.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("不正なアクセスです");
        }

        StockTrade trade = new StockTrade();
        trade.setStock(stock);
        trade.setUser(user);
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        trade.setAccountType(request.getAccountType());
        trade.setTradeDate(request.getTradeDate());
        trade.setTradeType(TradeType.BUY);

        stockTradeRepository.save(trade);
    }

    public void sellStock(User user, SellRequest request) {

        // ① 入力バリデーション
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量は1以上で入力してください");
        }

        if (request.getTradeDate() == null) {
            throw new IllegalArgumentException("売却日を入力してください");
        }

        if (request.getPrice() == null || 
            request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("価格は正の値を入力してください");
        }

        // ② 銘柄取得（必ず user 境界をかける）
        Stock stock = stockRepository
                .findByIdAndUser_Id(request.getStockId(), user.getId())
                .orElseThrow(() -> 
                        new IllegalArgumentException("銘柄が存在しません"));

        // ③ 現在保有数量を計算
        Integer currentQuantity =
                stockTradeRepository.calculateHoldingQuantity(
                        user,
                        stock,
                        request.getAccountType()
                );

        if (currentQuantity == null) {
            currentQuantity = 0;
        }

        // ④ 保有数量超過チェック
        if (request.getQuantity() > currentQuantity) {
            throw new IllegalArgumentException(
                    "保有数量を超えて売却できません（現在：" 
                    + currentQuantity + "株）");
        }

        AccountType accountType =
                stockTradeRepository
                        .findTopByUserAndStockOrderByTradeDateAsc(user, stock)
                        .orElseThrow(() ->
                                new IllegalArgumentException("取引履歴が存在しません"))
                        .getAccountType();

        // ① 平均取得単価を取得
        BigDecimal averagePrice =
                calculateAveragePriceForSell(
                        user,
                        stock.getStockCode(),
                        accountType
                );
        // ② 実現損益を計算
        BigDecimal profit =
                request.getPrice()
                        .subtract(averagePrice)
                        .multiply(BigDecimal.valueOf(request.getQuantity()));

        // ⑤ SELL履歴を追加（数量は正で保存）
        StockTrade trade = new StockTrade();
        trade.setStock(stock);
        trade.setUser(user);
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        trade.setTradeDate(request.getTradeDate());
        trade.setTradeType(TradeType.SELL);
        trade.setRealizedProfit(profit);
        trade.setAccountType(request.getAccountType());

        stockTradeRepository.save(trade);
    }

    public List<StockTrade> getSellTrades(User user) {
    return stockTradeRepository
            .findByUserAndTradeType(user, TradeType.SELL);
    }

    public BigDecimal calculateAveragePriceForSell(
            User user,
            String stockCode,
            AccountType accountType) {

        List<StockTrade> trades =
                stockTradeRepository
                        .findByUserAndStock_StockCodeAndAccountTypeOrderByTradeDateAsc(
                                user,
                                stockCode,
                                accountType
                        );

    int totalBuyQuantity = 0;
    BigDecimal totalBuyAmount = BigDecimal.ZERO;

    for (StockTrade trade : trades) {
        if (trade.getTradeType() == TradeType.BUY) {
            totalBuyQuantity += trade.getQuantity();
            totalBuyAmount = totalBuyAmount.add(
                    trade.getPrice()
                            .multiply(BigDecimal.valueOf(trade.getQuantity()))
            );
        }
    }

    if (totalBuyQuantity == 0) {
        throw new IllegalStateException("購入履歴がありません");
    }

    return totalBuyAmount.divide(
            BigDecimal.valueOf(totalBuyQuantity),
            2,
            RoundingMode.HALF_UP
        );
    }

    public List<StockTrade> getAllTrades(User user) {
        return stockTradeRepository
            .findByUserOrderByTradeDateDesc(user);
    }

    public BigDecimal getTotalRealizedProfit(User user) {
        return stockTradeRepository.sumRealizedProfitByUser(user);
    }



    public StockHoldingDto getStockHolding(Long stockId) {

    Stock stock = stockRepository.findById(stockId)
            .orElseThrow();

    List<StockTrade> trades =
            stockTradeRepository.findByStockId(stockId);

    int quantity = 0;
    BigDecimal totalBuy = BigDecimal.ZERO;

    for (StockTrade t : trades) {

        if (t.getTradeType() == TradeType.BUY) {

            quantity += t.getQuantity();

            totalBuy = totalBuy.add(
                    t.getPrice().multiply(
                            BigDecimal.valueOf(t.getQuantity())
                    )
            );
        }

        if (t.getTradeType() == TradeType.SELL) {

            quantity -= t.getQuantity();
        }
    }

    BigDecimal avgPrice = BigDecimal.ZERO;

    if (quantity > 0) {
        avgPrice = totalBuy.divide(
                BigDecimal.valueOf(quantity),
                0,
                RoundingMode.HALF_UP
        );
    }

    StockHoldingDto dto = new StockHoldingDto();

        dto.setStockId(stock.getId());
        dto.setStockCode(stock.getStockCode());
        dto.setStockName(stock.getStockName());

        dto.setQuantity(quantity);
        dto.setAveragePrice(avgPrice);

        dto.setTotalBuyAmount(totalBuy);

        dto.setHoldingAmount(
                avgPrice.multiply(BigDecimal.valueOf(quantity))
        );

        return dto;
    }

}

