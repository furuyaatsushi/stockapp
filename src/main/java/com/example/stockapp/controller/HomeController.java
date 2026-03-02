package com.example.stockapp.controller;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stockapp.entity.User;
import com.example.stockapp.service.StockTradeService;
import com.example.stockapp.service.UserService;


@Controller
public class HomeController {

    private final StockTradeService stockTradeService;
    private final UserService userService;

    public HomeController(
            StockTradeService stockTradeService,
            UserService userService) {

        this.stockTradeService = stockTradeService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {

        String username = principal.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("realizedProfit",
                stockTradeService.getTotalRealizedProfit(user));

        // 他はまだ未実装なら仮で0
        model.addAttribute("totalValuation", BigDecimal.ZERO);
        model.addAttribute("totalCost", BigDecimal.ZERO);
        model.addAttribute("unrealizedProfit", BigDecimal.ZERO);

        return "index";
    }
}