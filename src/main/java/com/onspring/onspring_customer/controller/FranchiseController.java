package com.onspring.onspring_customer.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("uiFranchiseController")
@RequestMapping("/franchise")
public class FranchiseController {

    @GetMapping("/add")
    public String addFranchise(Model model) {
        model.addAttribute("franchise",  "franchise-add");
        return "/franchise/add";
    }

    @GetMapping("/manage")
    public String manageFranchise(Model model) {
        model.addAttribute("franchise",  "franchise-manage");
        return "/franchise/manage";
    }

    @GetMapping("/activated")
    public String activatedFranchise(Model model) {
        model.addAttribute("franchise",  "franchise-activated");
        return "/franchise/activated";
    }
}
