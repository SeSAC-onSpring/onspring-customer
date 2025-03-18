package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final FranchiseRepository franchiseRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final ModelMapper modelMapper;

    private Customer getCustomer(Long id) {
        Optional<Customer> result = customerRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found"));
    }

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, FranchiseRepository franchiseRepository,
                               CustomerFranchiseRepository customerFranchiseRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.franchiseRepository = franchiseRepository;
        this.customerFranchiseRepository = customerFranchiseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Long saveCustomer(CustomerDto customerDto) {
        log.info("Saving customer with name {}", customerDto.getName());

        Customer customer = modelMapper.map(customerDto, Customer.class);
        Long id = customerRepository.save(customer)
                .getId();

        log.info("Successfully saved customer with name {}", customer.getName());

        return id;
    }

    @Override
    public CustomerDto findCustomerById(Long id) {
        Customer customer = getCustomer(id);

        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findAllCustomer() {
        return List.of();
    }

    @Override
    public boolean updateCustomer(CustomerDto customerDto) {
        log.info("Updating customer with ID {}", customerDto.getId());

        Customer customer = getCustomer(customerDto.getId());

        modelMapper.map(customerDto, customer);
        customerRepository.save(customer);

        log.info("Successfully updated customer with ID {}", customerDto.getId());

        return true;
    }

    @Override
    public Long addFranchiseToCustomer(Long customerId, Long franchiseId) {
        log.info("Adding franchise with franchise ID {} to customer id {}", franchiseId, customerId);

        Customer customer = getCustomer(customerId);

        Optional<Franchise> franchiseResult = franchiseRepository.findById(franchiseId);
        Franchise franchise =
                franchiseResult.orElseThrow(() -> new EntityNotFoundException("Franchise with ID " + franchiseId + " "
                        + "not found"));

        CustomerFranchise customerFranchise = new CustomerFranchise();
        customerFranchise.setCustomer(customer);
        customerFranchise.setFranchise(franchise);

        Long id = customerFranchiseRepository.save(customerFranchise)
                .getId();

        log.info("Successfully added franchise with user ID {} to customer id {}", franchiseId, customerId);

        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        customer.setPhone(customerDto.getPhone());
        customer.setActivated(customerDto.isActivated());
        customerRepository.save(customer);

        return true;
    }

    @Override
    public boolean deactivateCustomer(Long id) {
        log.info("Deactivating customer with ID {}", id);

        Customer customer = getCustomer(id);
        customer.setActivated(false);

        customerRepository.save(customer);

        log.info("Successfully deactivated customer with ID {}", id);

        return false;
    }
}
