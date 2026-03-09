package com.example.stockapp.service;

import com.example.stockapp.dto.DividendRequest;
import com.example.stockapp.entity.*;
import com.example.stockapp.repository.*;

import org.springframework.stereotype.Service;

@Service
public class DividendService {

    private final DividendRepository dividendRepository;
    private final StockRepository stockRepository;

    public DividendService(
            DividendRepository dividendRepository,
            StockRepository stockRepository) {

        this.dividendRepository = dividendRepository;
        this.stockRepository = stockRepository;
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
}