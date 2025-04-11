package com.onspring.onspring_customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settlement")
public class SettlementController {

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("settlement",  "settlement-registration");
        return "settlement/registration";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("settlement",  "settlement-history");
        return "settlement/history";
    }
}
