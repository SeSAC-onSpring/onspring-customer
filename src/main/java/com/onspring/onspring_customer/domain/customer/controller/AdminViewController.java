package com.onspring.onspring_customer.domain.customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/admins")
public class AdminViewController {
    private final AdminService adminService;

    @PostMapping("/add")
    public String saveAdmin(@RequestParam(value = "customerId") Long customerId,
                            @RequestParam(value = "userName") String userName) {
        AdminDto adminDto = new AdminDto(null, customerId, userName, "*", false, true);

        adminService.saveAdmin(adminDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    public String findAdmins(@RequestParam(value = "customerId") Long customerId, @RequestParam(value = "userName",
            required = false) String userName, @RequestParam(value = "showDeactivated", defaultValue = "true",
            required = false) Boolean showDeactivated, @RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AdminDto> adminDtoPage = adminService.findAllAdminByQuery(customerId, userName, showDeactivated, pageable);

        model.addAttribute("customers", adminDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", adminDtoPage.getTotalPages());

        return "admins/list";
    }

    @PatchMapping("/update-password")
    public String updateAdminPassword(@RequestParam(value = "id") Long id,
                                      @RequestParam(value = "password") String password) {
        adminService.updateAdminPasswordById(id, password);

        return "redirect:list";
    }

    @PatchMapping("/activate")
    public String activateAdmin(@RequestParam(value = "id") Long id) {
        adminService.activateAdminById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    public String deactivateAdmin(@RequestParam(value = "id") Long id) {
        adminService.deactivateAdminById(id);

        return "redirect:list";
    }
}
