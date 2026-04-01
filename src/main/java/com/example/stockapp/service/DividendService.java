package com.example.stockapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.stockapp.dto.DividendRequest;
import com.example.stockapp.dto.DividendYearlyDto;
import com.example.stockapp.entity.Dividend;
import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.User;
import com.example.stockapp.repository.DividendRepository;
import com.example.stockapp.repository.StockRepository;
import com.example.stockapp.repository.StockTradeRepository;

@Service
public class DividendService {

    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;
    private final StockTradeRepository stockTradeRepository;

    public DividendService(
            DividendRepository dividendRepository,
            StockRepository stockRepository,
            StockTradeRepository stockTradeRepository) {

        this.dividendRepository = dividendRepository;
        this.stockRepository = stockRepository;
        this.stockTradeRepository = stockTradeRepository;
    }

    public void registerDividend(User user, DividendRequest request) {

        // 銘柄取得
        Stock stock = stockRepository
                .findByIdAndUser_Id(request.getStockId(), user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("銘柄が存在しません"));

        Dividend dividend = new Dividend();
        dividend.setUser(user);
        dividend.setStock(stock);
        dividend.setAccountType(request.getAccountType());
        dividend.setAmount(request.getAmount());
        dividend.setDividendDate(request.getDividendDate());
        dividend.setMemo(request.getMemo());

        dividendRepository.save(dividend);
    }

    public List<DividendYearlyDto> getYearlyYieldByStock(User user, Long stockId) {

    List<StockTrade> trades =
        stockTradeRepository.findByUser_IdAndStock_Id(user.getId(), stockId);

    List<Dividend> dividends =
        dividendRepository.findByUser_IdAndStock_Id(user.getId(), stockId);

    // ------------------------
    // ① 配当：年ごと集計
    // ------------------------
    Map<Integer, BigDecimal> dividendMap = dividends.stream()
            .collect(Collectors.groupingBy(
                    d -> d.getDividendDate().getYear(),
                    Collectors.reducing(BigDecimal.ZERO, Dividend::getAmount, BigDecimal::add)
            ));

    // ------------------------
    // ② 投資額：累計
    // ------------------------
    trades.sort(Comparator.comparing(StockTrade::getTradeDate));

    Map<Integer, BigDecimal> investmentMap = new HashMap<>();
    BigDecimal cumulative = BigDecimal.ZERO;

    for (StockTrade t : trades) {

        BigDecimal amount = t.getPrice()
                .multiply(BigDecimal.valueOf(t.getQuantity()));

        cumulative = cumulative.add(amount);

        int year = t.getTradeDate().getYear();

        investmentMap.put(year, cumulative);
    }

    // ------------------------
    // ③ DTO作成
    // ------------------------
    List<DividendYearlyDto> result = new ArrayList<>();

    for (Integer year : dividendMap.keySet()) {

        BigDecimal dividend = dividendMap.get(year);

        // その年までの最大累計投資額
        BigDecimal investment = investmentMap.entrySet().stream()
                .filter(e -> e.getKey() <= year)
                .map(Map.Entry::getValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal yield = BigDecimal.ZERO;

        if (investment.compareTo(BigDecimal.ZERO) > 0) {
            yield = dividend
                    .divide(investment, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        DividendYearlyDto dto = new DividendYearlyDto();
        dto.setYear(year);
        dto.setTotalDividend(dividend);
        dto.setTotalInvestment(investment);
        dto.setYield(yield);

        result.add(dto);
    }

    result.sort(Comparator.comparing(DividendYearlyDto::getYear));

    return result;
}
}