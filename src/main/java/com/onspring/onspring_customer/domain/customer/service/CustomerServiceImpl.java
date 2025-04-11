package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.QCustomer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final FranchiseRepository franchiseRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

    private Customer getCustomer(Long id) {
        Optional<Customer> result = customerRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found"));
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
        return customerRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, CustomerDto.class))
                .toList();
    }

    @Override
    public Page<CustomerDto> findAllCustomerByQuery(String name, String address, String phone, boolean isActivated,
                                                    Pageable pageable) {
        QCustomer customer = QCustomer.customer;
        JPAQuery<Customer> query = queryFactory.selectFrom(customer);

        if (name != null) {
            query.where(customer.name.containsIgnoreCase(name));
        }
        if (address != null) {
            query.where(customer.address.containsIgnoreCase(address));
        }
        if (phone != null) {
            query.where(customer.phone.contains(phone));
        }

        query.where(customer.isActivated);

        Long count = Objects.requireNonNull(query.clone()
                .select(customer.count())
                .fetchOne());

        query.orderBy(customer.id.desc());

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Customer> customerList = query.fetch();

        List<CustomerDto> customerDtoList = customerList.stream()
                .map(element -> modelMapper.map(element, CustomerDto.class))
                .toList();

        return new PageImpl<>(customerDtoList, pageable, count);
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

        return id;
    }

    @Override
    public boolean deleteFranchiseFromCustomer(Long id) {
        log.info("Deleting franchise from customer id {}", id);

        Optional<CustomerFranchise> result = customerFranchiseRepository.findById(id);
        CustomerFranchise customerFranchise = result.orElseThrow(() -> new EntityNotFoundException("CustomerFranchise"
                                                                                                   + " with ID " + id + " not found"));

        Long customerId = customerFranchise.getCustomer()
                .getId();
        Long franchiseId = customerFranchise.getFranchise()
                .getId();
        customerFranchiseRepository.delete(customerFranchise);

        log.info("Successfully deleted franchise id {} from customer id {}", franchiseId, customerId);

        return true;
    }

    @Override
    public boolean activateCustomerById(Long id) {
        log.info("Activating customer with ID {}", id);

        Customer customer = getCustomer(id);
        customer.setActivated(true);

        customerRepository.save(customer);

        log.info("Successfully activated customer with ID {}", id);

        return true;
    }

    @Override
    public boolean deactivateCustomerById(Long id) {
        log.info("Deactivating customer with ID {}", id);

        Customer customer = getCustomer(id);
        customer.setActivated(false);

        customerRepository.save(customer);

        log.info("Successfully deactivated customer with ID {}", id);

        return true;
    }
}
