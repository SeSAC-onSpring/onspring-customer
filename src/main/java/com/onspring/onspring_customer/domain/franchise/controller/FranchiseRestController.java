package com.onspring.onspring_customer.domain.franchise.controller;

import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/franchises")
public class FranchiseRestController {
    private final FranchiseService franchiseService;

    @Autowired
    public FranchiseRestController(FranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    @PatchMapping("/activate")
    void activateFranchise(@RequestParam(value = "ids") List<Long> ids) {
        franchiseService.activateFranchiseById(ids);
    }

    @PatchMapping("/deactivate")
    void deactivateFranchise(@RequestParam(value = "ids") List<Long> ids) {
        franchiseService.deactivateFranchiseById(ids);
    }
}
