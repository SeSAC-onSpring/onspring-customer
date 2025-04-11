package com.onspring.onspring_customer.domain.franchise.controller;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/franchises")
public class FranchiseViewController {
    private final FranchiseService franchiseService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/add")
    String showSaveFranchise() {
        return "franchises/add";
    }

    @PostMapping("/add")
    String saveFranchise(@RequestParam(value = "userName") String userName, @RequestParam(value = "name") String name
            , @RequestParam(value = "ownerName") String ownerName,
                         @RequestParam(value = "businessNumber") String businessNumber, @RequestParam(value =
                    "address") String address, @RequestParam(value = "phone") String phone, @RequestParam(value =
                    "password") String password) {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .userName(userName)
                .name(name)
                .ownerName(ownerName)
                .businessNumber(businessNumber)
                .address(address)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .build();
        franchiseService.saveFranchise(franchiseDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getFranchises(@RequestParam(value = "searchType", required = false) String searchType,
                         @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value =
                    "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FranchiseDto> franchiseDtoPage;
        if (searchType == null) {
            franchiseDtoPage = franchiseService.findAllFranchiseByQuery(null, null, null, null, null, null, null,
                    true, pageable);
        } else {
            franchiseDtoPage = switch (searchType) {
                case "name" ->
                        franchiseService.findAllFranchiseByQuery(null, keyword, null, null, null, null, null, true,
                                pageable);
                case "businessNumber" ->
                        franchiseService.findAllFranchiseByQuery(null, null, null, keyword, null, null, null, true,
                                pageable);
                case "ownerName" ->
                        franchiseService.findAllFranchiseByQuery(null, null, keyword, null, null, null, null, true,
                                pageable);
                case "phone" ->
                        franchiseService.findAllFranchiseByQuery(null, null, null, null, null, keyword, null, true,
                                pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        }

        model.addAttribute("franchises", franchiseDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", franchiseDtoPage.getTotalPages());

        return "franchises/list";
    }

    @GetMapping("/update/{id}")
    public String showUpdateFranchise(@PathVariable Long id, Model model) {
        FranchiseDto franchise = franchiseService.findFranchiseById(id);

        model.addAttribute("franchise", franchise);

        return "franchises/update";
    }

    @PutMapping("/update/{id}")
    String updateFranchise(@PathVariable Long id, @RequestParam(value = "name") String name,
                           @RequestParam(value = "ownerName") String ownerName, @RequestParam(value = "address",
                    required = false) String address, @RequestParam(value = "phone") String phone) {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .id(id)
                .name(name)
                .ownerName(ownerName)
                .address(address)
                .phone(phone)
                .build();
        franchiseService.updateFranchise(id, franchiseDto);

        return "redirect:list";
    }

    @GetMapping("/activate")
    String showActivateFranchise(@RequestParam(value = "userName", required = false) String userName,
                                 @RequestParam(value = "name", required = false) String name, @RequestParam(value =
                    "ownerName", required = false) String ownerName, @RequestParam(value = "businessNumber",
                    required = false) String businessNumber,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "phone", required = false) String phone, @RequestParam(value =
                    "customerName", required = false) String customerName, @RequestParam(value = "page",
                    defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size
            , Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FranchiseDto> franchiseDtoPage = franchiseService.findAllFranchiseByQuery(userName, name, ownerName,
                businessNumber, address, phone, customerName, false, pageable);

        model.addAttribute("franchises", franchiseDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", franchiseDtoPage.getTotalPages());

        return "franchises/activate";
    }
}
