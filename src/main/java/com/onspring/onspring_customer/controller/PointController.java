package com.onspring.onspring_customer.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/points")
public class PointController {

    @GetMapping("issue")
    public String issue(Model model) {
        model.addAttribute("activeMenu", "point-issue");
        return "points/issue";
    }

    @GetMapping("modify")
    public String modify(Model model) {
        model.addAttribute("activeMenu", "point-modify");
        return "points/modify";
    }
}
