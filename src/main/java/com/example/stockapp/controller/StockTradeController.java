package com.example.stockapp.controller;

import com.example.stockapp.entity.StockTrade;
import com.example.stockapp.entity.User;
import com.example.stockapp.repository.UserRepository;
import com.example.stockapp.service.StockTradeService;
import com.example.stockapp.dto.StockTradeDto;
import com.example.stockapp.dto.StockHoldingDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class StockTradeController {

    private final StockTradeService stockTradeService;
    private final UserRepository userRepository;

    public StockTradeController(
            StockTradeService stockTradeService,
            UserRepository userRepository) {
        this.stockTradeService = stockTradeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<StockTradeDto> getMyTrades(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        return stockTradeService.getTradeDtosByUser(user);
    }

    @GetMapping("/holdings")
    public List<StockHoldingDto> getMyHoldings(
        @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository
            .findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        return stockTradeService.getCurrentHoldings(user);
    }

}
