package com.onspring.onspring_customer.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/party")
public class PartyController {

    @GetMapping("/add")
    public String addMember(Model model) {
        model.addAttribute("activeMenu", "party-add");
        return "/party/add";
    }

    @GetMapping("/manage")
    public String manageMember(Model model) {
        model.addAttribute("activeMenu", "party-manage");
        return "/party/manage";
    }

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("activeMenu", "party-members");
        return "/party/members";
    }


}
