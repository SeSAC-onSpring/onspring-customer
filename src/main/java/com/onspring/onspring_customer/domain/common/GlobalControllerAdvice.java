package com.onspring.onspring_customer.domain.common;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerAdvice {

    private final AdminService adminService;

    @ModelAttribute
    public void addUserNameToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인한 사용자만 처리
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Long adminId = SecurityUtil.getCurrentUserId();
                AdminDto adminDto = adminService.findAdminById(adminId);
                model.addAttribute("userName", adminDto.getUserName());
            } catch (Exception e) {
                model.addAttribute("userName", "Guest");
            }
        } else {
            // 비로그인 상태일 경우 (Guest로 처리 등)
            model.addAttribute("userName", "Guest");
        }
    }
}