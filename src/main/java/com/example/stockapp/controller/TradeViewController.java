package com.example.stockapp.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.stockapp.dto.AddBuyRequest;
import com.example.stockapp.dto.DividendYearlyDto;
import com.example.stockapp.dto.NewBuyRequest;
import com.example.stockapp.dto.SellRequest;
import com.example.stockapp.dto.StockHoldingDto;
import com.example.stockapp.entity.AccountType;
import com.example.stockapp.entity.Dividend;
import com.example.stockapp.entity.Stock;
import com.example.stockapp.entity.User;
import com.example.stockapp.repository.DividendRepository;
import com.example.stockapp.repository.StockRepository;
import com.example.stockapp.repository.UserRepository;
import com.example.stockapp.service.DividendService;
import com.example.stockapp.service.StockTradeService;

@Controller
public class TradeViewController {

    private final StockTradeService stockTradeService;
    private final DividendService dividendService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final DividendRepository dividendRepository;

    public TradeViewController(
            StockTradeService stockTradeService,
            DividendService dividendService,
            UserRepository userRepository,
            StockRepository stockRepository,
            DividendRepository dividendRepository) {
        this.stockTradeService = stockTradeService;
        this.dividendService = dividendService;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.dividendRepository = dividendRepository;
    }

    @GetMapping("/stocks")
    public String trades(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        List<StockHoldingDto> holdings =
                stockTradeService.getCurrentHoldings(user);

        BigDecimal totalHoldingAmount =
            holdings.stream()
                .map(StockHoldingDto::getHoldingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("holdings", holdings);
        model.addAttribute("totalHoldingAmount", totalHoldingAmount);
        return "stocks/list";
    }

    @GetMapping("/trades/buy/new")
    public String newBuyForm(Model model) {
        model.addAttribute("newBuyRequest", new NewBuyRequest());
        return "trades/buy-new";
    }


    @PostMapping("/trades/buy/new")
    public String buyNewSubmit(
            @AuthenticationPrincipal UserDetails userDetails,
            NewBuyRequest request
    ) {
        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        stockTradeService.buyNewStock(user, request);

        return "redirect:/stocks";
    }

    @GetMapping("/trades/buy/add/{stockId}/{accountType}")
    public String showAddBuyForm(
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

        AddBuyRequest request = new AddBuyRequest();
        request.setStockId(stockId);
        request.setAccountType(accountType);

        model.addAttribute("stock", stock);
        model.addAttribute("accountType", accountType);
        model.addAttribute("addBuyRequest", request);

        return "trades/buy-add";
    }

    @PostMapping("/trades/buy/add")
    public String buyAddSubmit(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute AddBuyRequest addBuyRequest
    ) {

        System.out.println("stockId = " + addBuyRequest.getStockId());
        System.out.println("accountType = " + addBuyRequest.getAccountType());

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        stockTradeService.buyAdditionalStock(user, addBuyRequest);

        return "redirect:/stocks";
    }

    @GetMapping("/trades/sell")
    public String showSellForm(Model model,
                                @AuthenticationPrincipal  UserDetails userDetails) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        List<StockHoldingDto> holdings =
                stockTradeService.getCurrentHoldings(user);

        model.addAttribute("holdings", holdings);
        model.addAttribute("sellRequest", new SellRequest());

        return "trades/sell";
    }

    @PostMapping("/trades/sell")
    public String sellStock(@AuthenticationPrincipal UserDetails userDetails,
                @ModelAttribute SellRequest request,
                Model model) {
        System.out.println("quantity = " + request.getQuantity());
        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        try {
                stockTradeService.sellStock(user, request);
                return "redirect:/stocks";

        } catch (IllegalArgumentException e) {

                model.addAttribute("stocks",
                stockTradeService.getCurrentHoldings(user));

                model.addAttribute("errorMessage", e.getMessage());
                return "trades/sell";
        }
    }

    @GetMapping("/trades/sell-history")
        public String showSellHistory(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        model.addAttribute("sellTrades",
                stockTradeService.getSellTrades(user));
        
        model.addAttribute("totalProfit",
        stockTradeService.getTotalRealizedProfit(user));

        return "trades/sell-history";
    }

    @GetMapping("/trades/history")
    public String showTradeHistory(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        model.addAttribute("trades",
                stockTradeService.getAllTrades(user));

        return "trades/history";
    }

    @GetMapping("/stocks/{id}")
    public String stockDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("銘柄が見つかりません"));

        // ★ ここ追加
        List<StockHoldingDto> holdings =
                stockTradeService.getHoldingsByStock(user, id);

        List<Dividend> dividends =
                dividendRepository.findByStockIdOrderByDividendDateDesc(id);

        List<DividendYearlyDto> yearlyYield =
            dividendService.getYearlyYieldByStock(user, id);

        model.addAttribute("stock", stock);
        model.addAttribute("holdings", holdings); // ★追加
        model.addAttribute("dividends", dividends);
        model.addAttribute("yearlyYield", yearlyYield);

        return "stocks/detail";
    }
}
