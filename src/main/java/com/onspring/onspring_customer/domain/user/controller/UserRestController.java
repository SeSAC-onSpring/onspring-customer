package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/view/users")
public class UserRestController {
    private final EndUserService endUserService;

    @Autowired
    public UserRestController(EndUserService endUserService) {
        this.endUserService = endUserService;
    }

    @PatchMapping("/activate")
    void activateUser(@RequestParam(value = "ids") List<Long> ids) {
        endUserService.activateEndUserById(ids);
    }

    @PatchMapping("/deactivate")
    void deactivateUser(@RequestParam(value = "ids") List<Long> ids) {
        endUserService.deactivateEndUserById(ids);
    }

    @GetMapping("/users")
    List<EndUserDto> getUsers() {
        return endUserService.findAllEndUser();
    }
}
