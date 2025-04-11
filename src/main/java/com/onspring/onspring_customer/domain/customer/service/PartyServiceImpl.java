package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.dto.PartyEndUserRelationDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class PartyServiceImpl implements PartyService {
    private final PointRepository pointRepository;
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

    private Party getParty(Long id) {
        Optional<Party> result = partyRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    private List<EndUser> getEndUser(List<Long> ids) {
        return endUserRepository.findAllById(ids);
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

        PartyDto partyDto = modelMapper.map(party, PartyDto.class);
        partyDto.setCustomerId(party.getCustomer()
                .getId());
        partyDto.setEndUserIds(party.getPoints()
                .stream()
                .map(Point::getEndUser)
                .map(EndUser::getId)
                .toList());

        return partyDto;
    }

    /**
     * 사용자가 속한 가장 오래된 party의 정보를 가져옴
     * @param userId
     * @return partyDto
     */
    @Override
    public PartyDto findPartyByUserId(Long userId) {
        Party party = partyRepository.findOldestPartyByEndUserId(userId).get(0);
        PartyDto partyDto = modelMapper.map(party, PartyDto.class);

        return partyDto;
    }

    @Override
    public List<PartyDto> findAllParty() {
        return partyRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, PartyDto.class))
                .toList();
    }

    @Override
    public Page<PartyDto> findAllPartyByQuery(String name, LocalTime allowedTimeStart, LocalTime allowedTimeEnd,
                                              boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                                              boolean thursday, boolean friday, boolean saturday,
                                              BigDecimal maximumAmount, Long maximumTransaction, Pageable pageable) {
        QParty party = QParty.party;
        JPAQuery<Party> query = queryFactory.selectFrom(party);

        if (name != null) {
            query.where(party.name.containsIgnoreCase(name));
        }
        if (allowedTimeStart != null && allowedTimeEnd != null) {
            query.where(party.allowedTimeStart.before(allowedTimeStart)
                    .and(party.allowedTimeEnd.after(allowedTimeEnd)));
        }
        if (maximumAmount != null) {
            query.where(party.maximumAmount.loe(maximumAmount));
        }
        if (maximumTransaction != null) {
            query.where(party.maximumTransaction.loe(maximumTransaction));
        }
        if (sunday || monday || tuesday || wednesday || thursday || friday || saturday) {
            query.where(party.sunday.eq(sunday));
            query.where(party.monday.eq(monday));
            query.where(party.tuesday.eq(tuesday));
            query.where(party.wednesday.eq(wednesday));
            query.where(party.thursday.eq(thursday));
            query.where(party.friday.eq(friday));
            query.where(party.saturday.eq(saturday));
        }

        Long count = Objects.requireNonNull(query.clone()
                .select(party.count())
                .fetchOne());

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Party> partyList = query.fetch();

        List<PartyDto> partyDtoList = new ArrayList<>();

        for (Party element : partyList) {
            PartyDto map = modelMapper.map(element, PartyDto.class);
            map.setEndUserIds(element.getPoints()
                    .stream()
                    .map(Point::getEndUser)
                    .map(EndUser::getId)
                    .toList());
            partyDtoList.add(map);
        }

        return new PageImpl<>(partyDtoList, pageable, count);
    }

    @Override
    public Page<PartyEndUserRelationDto> findAllPartyEndUserRelationByQuery(String name, Pageable pageable) {
        Map<Long, List<Point>> pointMap = pointRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(point -> point.getParty()
                        .getId()));

        List<PartyEndUserRelationDto> partyEndUserRelationDtoList = pointMap.values()
                .stream()
                .map(points -> {

                    PartyDto partyDto = modelMapper.map(points.stream()
                            .findFirst()
                            .orElseThrow()
                            .getParty(), PartyDto.class);
                    List<EndUserDto> endUserDtoList = points.stream()
                            .map(Point::getEndUser)
                            .map(element -> modelMapper.map(element, EndUserDto.class))
                            .toList();

                    return new PartyEndUserRelationDto(partyDto, endUserDtoList);
                })
                .toList();

        return new PageImpl<>(partyEndUserRelationDtoList, pageable, partyEndUserRelationDtoList.size());
    }

    @Override
    public List<Long> addEndUserToParty(Long partyId, List<Long> endUserIds) {
        log.info("Adding points with end users ID {} to party ID {}", endUserIds, partyId);

        Party party = getParty(partyId);
        List<EndUser> endUserList = getEndUser(endUserIds);

        List<Point> points = endUserList.stream()
                .map(endUser -> Point.builder()
                        .party(party)
                        .endUser(endUser)
                        .assignedAmount(BigDecimal.ZERO)
                        .currentAmount(BigDecimal.ZERO)
                        .build())
                .toList();
        pointRepository.saveAll(points);

        log.info("Successfully added points with end users ID {} to party id {}", endUserIds, partyId);

        return points.stream()
                .map(Point::getId)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateParty(PartyDto partyDto) {
        log.info("Updating party with ID {}", partyDto.getId());

        Party party = getParty(partyDto.getId());

        // Customer 관계 유지
        Customer customer = party.getCustomer();

        // 기본 필드 업데이트
        party.setName(partyDto.getName());
        party.setPeriod(partyDto.getPeriod());
        party.setAmount(partyDto.getAmount());
        party.setAllowedTimeStart(partyDto.getAllowedTimeStart());
        party.setAllowedTimeEnd(partyDto.getAllowedTimeEnd());
        party.setValidThru(partyDto.getValidThru());
        party.setSunday(partyDto.isSunday());
        party.setMonday(partyDto.isMonday());
        party.setTuesday(partyDto.isTuesday());
        party.setWednesday(partyDto.isWednesday());
        party.setThursday(partyDto.isThursday());
        party.setFriday(partyDto.isFriday());
        party.setSaturday(partyDto.isSaturday());
        party.setMaximumAmount(partyDto.getMaximumAmount());
        party.setMaximumTransaction(partyDto.getMaximumTransaction());

        // customer 관계 유지
        party.setCustomer(customer);

        partyRepository.save(party);

        log.info("Successfully updated party with ID {}", partyDto.getId());

        return true;
    }

    @Override
    public boolean activatePartyById(Long id) {
        log.info("Activating party with ID {}", id);

        Party party = getParty(id);

        party.setActivated(true);
        partyRepository.save(party);

        log.info("Successfully activated party with ID {}", id);

        return true;
    }

    @Override
    public boolean deactivatePartyById(Long id) {
        log.info("Deactivating party with ID {}", id);

        Party party = getParty(id);

        party.setActivated(false);
        partyRepository.save(party);

        log.info("Successfully deactivated party with ID {}", id);

        return true;
    }
}