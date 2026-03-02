package com.example.stockapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stockapp.dto.StockHoldingDto;
import com.example.stockapp.service.StockTradeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HoldingController {

    private final StockTradeService stockTradeService;

    @GetMapping("/holdings")
    public String showHoldings(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
        Model model) {

        List<StockHoldingDto> holdings =
                stockTradeService.getCurrentHoldingsByUsername(
                        principal.getUsername()
                );

        model.addAttribute("holdings", holdings);
        return "holdings";
    }
}
