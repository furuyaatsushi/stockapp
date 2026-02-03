package com.example.stockapp.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, Principal principal, Authentication authentication) {

         // ログイン中のユーザー名
        String username = principal.getName();
        System.out.println("login user = " + authentication.getName());

        model.addAttribute("username", username);

        return "index"; // templates/index.html を返す
    }

}