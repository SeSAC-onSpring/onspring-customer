package com.onspring.onspring_customer.domain.common.controller;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;
import com.onspring.onspring_customer.domain.common.service.PlatformAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/platform-admins")
public class PlatformAdminViewController {
    private final PlatformAdminService platformAdminService;


    @GetMapping("/add")
    public String showAddPlatformAdminForm() {
        return "platform-admins/add";
    }

    @PostMapping("/add")
    String savePlatformAdmin(@RequestParam(value = "userName") String userName) {
        PlatformAdminDto platformAdminDto = new PlatformAdminDto(null, userName, "*", false, true);

        platformAdminService.savePlatformAdmin(platformAdminDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getPlatformAdmins(@RequestParam(value = "userName", required = false) String userName,
                             @RequestParam(value = "showDeactivated", defaultValue = "true", required = false) Boolean showDeactivated
            , @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size",
                    defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PlatformAdminDto> platformAdminDtoPage = platformAdminService.findAllPlatformAdminByQuery(userName,
                showDeactivated, pageable);

        model.addAttribute("platformAdmins", platformAdminDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", platformAdminDtoPage.getTotalPages());

        return "platform-admins/list";
    }

    @PatchMapping("/update-password")
    String updatePlatformAdminPassword(@RequestParam(value = "id") Long id,
                                       @RequestParam(value = "password") String password) {
        platformAdminService.updatePlatformAdminPasswordById(id, password);

        return "redirect:list";
    }

    @PatchMapping("/activate")
    String activatePlatformAdmin(@RequestParam(value = "id") Long id) {
        platformAdminService.activatePlatformAdminById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    String deactivatePlatformAdmin(@RequestParam(value = "id") Long id) {
        platformAdminService.deactivatePlatformAdminById(id);

        return "redirect:list";
    }

    @DeleteMapping
    String deletePlatformAdmin(@RequestParam(value = "id") Long id) {
        platformAdminService.deletePlatformAdminById(id);

        return "redirect:list";
    }
}
