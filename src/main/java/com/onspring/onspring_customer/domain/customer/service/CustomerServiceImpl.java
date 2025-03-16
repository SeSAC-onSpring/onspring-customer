package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;

    @Override
    public Long saveCustomer(CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .address(customerDto.getAddress())
                .phone(customerDto.getPhone())
                .isActivated(customerDto.isActivated())
                .build();

        return customerRepository.save(customer)
                .getId();
    }

    @Override
    public CustomerDto findCustomerById(Long id) {
        return null;
    }

    @Override
    public List<CustomerDto> findAllCustomer() {
        return List.of();
    }

    @Override
    public boolean updateCustomer(CustomerDto customerDto) {
        return false;
    }

    @Override
    public boolean deleteCustomerById(Long id) {
        return false;
    }
}
