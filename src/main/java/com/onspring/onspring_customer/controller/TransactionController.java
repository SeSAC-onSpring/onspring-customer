package com.onspring.onspring_customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    @GetMapping("/franchise")
    public String franchise(Model model) {
        model.addAttribute("transaction",  "transaction-franchise");
        return "/transaction/franchise";
    }

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("transaction",  "transaction-members");
        return "/transaction/members";
    }

    @GetMapping("/party")
    public String party(Model model) {
        model.addAttribute("transaction",  "transaction-party");
        return "/transaction/party";
    }
}
