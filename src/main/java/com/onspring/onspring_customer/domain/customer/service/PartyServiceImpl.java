package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository, CustomerRepository customerRepository,
                            ModelMapper modelMapper) {
        this.partyRepository = partyRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private Party getParty(Long id) {
        Optional<Party> result = partyRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    @Override
    public Long saveParty(PartyDto partyDto) {
        log.info("Saving party with name {} associated with customer ID {}", partyDto.getName(),
                partyDto.getCustomerId());

        Customer customer = customerRepository.findById(partyDto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + partyDto.getCustomerId() + " not"
                        + " found"));

        Party party = modelMapper.map(partyDto, Party.class);
        party.setCustomer(customer);

        Long id = partyRepository.save(party)
                .getId();

        log.info("Successfully saved party with name {}", partyDto.getName());

        return id;
    }

    @Override
    public PartyDto findPartyById(Long id) {
        Party party = getParty(id);

        return PartyDto.builder()
                .id(party.getId())
                .customerId(party.getCustomer()
                        .getId())
                .name(party.getName())
                .period(party.getPeriod())
                .amount(party.getAmount())
                .allowedTimeStart(party.getAllowedTimeStart())
                .allowedTimeEnd(party.getAllowedTimeEnd())
                .validThru(party.getValidThru())
                .sunday(party.isSunday())
                .monday(party.isMonday())
                .tuesday(party.isTuesday())
                .wednesday(party.isWednesday())
                .thursday(party.isThursday())
                .friday(party.isFriday())
                .saturday(party.isSaturday())
                .maximumAmount(party.getMaximumAmount())
                .maximumTransaction(party.getMaximumTransaction())
                .isActivated(party.isActivated())
                .build();
    }

    @Override
    public List<PartyDto> findAllParty() {
        return List.of();
    }

    @Override
    public boolean updateParty(PartyDto partyDto) {
        log.info("Updating party with ID {}", partyDto.getId());

        Party party = getParty(partyDto.getId());

        modelMapper.map(partyDto, party);
        partyRepository.save(party);

        return true;
    }

    @Override
        return false;
    public boolean activatePartyById(Long id) {
        log.info("Activating party with ID {}", id);

        Party party = getParty(id);

        party.setActivated(true);
        partyRepository.save(party);

        log.info("Successfully activated party with ID {}", id);

        return true;
    }

    @Override
        return false;
    public boolean deactivatePartyById(Long id) {
    }
}
