package com.onspring.onspring_customer.domain.customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/view/customers")
public class CustomerViewController {
    private final CustomerService customerService;
    private final AdminService adminService;


    @PostMapping("/save")
    String saveCustomer(@RequestParam(value = "name") String name, @RequestParam(value = "address") String address,
                        @RequestParam(value = "phone") String phone) {
        CustomerDto customerDto = CustomerDto.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .build();

        Long customerId = customerService.saveCustomer(customerDto);

        AdminDto adminDto = new AdminDto(null, customerId, "admin", "*", true, true);

        adminService.saveAdmin(adminDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getCustomers(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "address",
            required = false) String address, @RequestParam(value = "phone", required = false) String phone,
                        @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size",
                    defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CustomerDto> customerDtoPage = customerService.findAllCustomerByQuery(name, address, phone, true,
                pageable);

        model.addAttribute("customers", customerDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerDtoPage.getTotalPages());

        return "customers/list";
    }

    @PutMapping("/update")
    String updateCustomer(@RequestParam(value = "name") String name, @RequestParam(value = "address") String address,
                          @RequestParam(value = "phone") String phone) {
        CustomerDto customerDto = CustomerDto.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .build();

        customerService.updateCustomer(customerDto);

        return "redirect:list";
    }

    @PostMapping("add-franchise")
    String addFranchiseToCustomer(@RequestParam(value = "customerId") Long customerId, @RequestParam(value =
            "franchiseId") Long franchiseId) {
//        TODO Implement

        return "redirect:list";
    }

    @DeleteMapping("delete-franchise")
    String deleteFranchiseFromCustomer(@RequestParam(value = "customerId") Long customerId, @RequestParam(value =
            "franchiseId") Long franchiseId) {
//        TODO Implementation

        return "redirect:list";
    }

    @PatchMapping("/activate")
    String activateCustomer(@RequestParam(value = "id") Long id) {
        customerService.activateCustomerById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    String deactivateCustomer(@RequestParam(value = "id") Long id) {
        customerService.deactivateCustomerById(id);

        return "redirect:list";
    }
}
