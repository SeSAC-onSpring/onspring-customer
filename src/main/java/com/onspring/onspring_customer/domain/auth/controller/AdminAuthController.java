package com.onspring.onspring_customer.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {

    @GetMapping("/view/login")
    public String loginPage() {
        return "auth/login"; // templates/login.html
    }

}
