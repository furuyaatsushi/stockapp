package com.example.stockapp.controller;

import com.example.stockapp.dto.DividendRequest;
import com.example.stockapp.entity.*;
import com.example.stockapp.repository.*;
import com.example.stockapp.service.DividendService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DividendController {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final DividendService dividendService;

    public DividendController(
            UserRepository userRepository,
            StockRepository stockRepository,
            DividendService dividendService) {

        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.dividendService = dividendService;
    }

    // 配当登録画面
    @GetMapping("/dividends/add/{stockId}/{accountType}")
    public String showDividendForm(
            @PathVariable Long stockId,
            @PathVariable AccountType accountType,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        Stock stock = stockRepository
                .findByIdAndUser_Id(stockId, user.getId())
                .orElseThrow();

        DividendRequest request = new DividendRequest();
        request.setStockId(stockId);
        request.setAccountType(accountType);

        model.addAttribute("stock", stock);
        model.addAttribute("accountType", accountType);
        model.addAttribute("dividendRequest", request);

        return "dividends/add";
    }

    // 配当登録処理
    @PostMapping("/dividends/add")
    public String registerDividend(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute DividendRequest request) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        dividendService.registerDividend(user, request);

        return "redirect:/stocks";
    }
}