package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    Long saveCustomer(CustomerDto customerDto);

    CustomerDto findCustomerById(Long id);

    List<CustomerDto> findAllCustomer();

    Page<CustomerDto> findAllCustomerByQuery(String name, String address, String phone, boolean isActivated,
                                             Pageable pageable);

    boolean updateCustomer(CustomerDto customerDto);

    Long addFranchiseToCustomer(Long customerId, Long franchiseId);

    boolean deleteFranchiseFromCustomer(Long id);

    boolean activateCustomerById(Long id);

    boolean deactivateCustomerById(Long id);
}
