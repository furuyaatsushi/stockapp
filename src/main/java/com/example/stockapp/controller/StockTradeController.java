package com.example.stockapp.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockapp.dto.StockHoldingDto;
import com.example.stockapp.dto.StockTradeDto;
import com.example.stockapp.dto.StockTradeRequest;
import com.example.stockapp.entity.User;
import com.example.stockapp.repository.UserRepository;
import com.example.stockapp.service.StockTradeService;

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

    @PostMapping(
        value = "/buy",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void buy(
        Principal principal,
        @RequestBody StockTradeRequest request
    ) {
        String username = principal.getName();
        System.out.println("username = " + username);

        User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        stockTradeService.buyStock(
            user,
            request.getStockCode(),
            request.getStockName(),
            request.getQuantity(),
            request.getPrice(),
            request.getTradeDate()
        );
    }

}
