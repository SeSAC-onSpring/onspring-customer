package com.onspring.onspring_customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@Log4j2
public class HomeController {

    private final AdminService adminService;

    @GetMapping("/view/home")
    public String home(Model model) {
        Long adiminId = SecurityUtil.getCurrentUserId();

        AdminDto adminDto = adminService.findAdminById(adiminId);
        log.info(adminDto.getUserName());

        model.addAttribute("userName", adminDto.getUserName());
        return "home";
    }

    @GetMapping("/members/add")
    public String addMember(Model model) {
        return "members/add";
    }

    @GetMapping("/members/manage")
    public String memberManagePage(Model model) {
        model.addAttribute("activeMenu", "member-manage");
        return "members/manage";
    }

    @GetMapping("/members/activated")
    public String activatedMember(Model model) {
        model.addAttribute("activeMenu", "member-activated");
        return "members/activated";
    }

    @GetMapping("/manager/changepassword")
    public String changePassword(Model model) {
        return "manager/changepassword";
    }



}
