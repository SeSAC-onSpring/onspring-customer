package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.entity.QEndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class EndUserServiceImpl implements EndUserService {
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;
    private final PointRepository pointRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;
    private final PasswordEncoder passwordEncoder;



    private EndUser getEndUser(Long id) {
        Optional<EndUser> result = endUserRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("EndUser with ID " + id + " not found"));
    }

    private Party getParty(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    @Override
    public Long saveEndUser(EndUserDto endUserDto) {
        log.info("Saving end user with name {} associated with party ID {}", endUserDto.getName(),
                endUserDto.getPartyIds()
                .get(0));

        EndUser endUser = modelMapper.map(endUserDto, EndUser.class);

        log.info(endUserDto.toString());

        Party party = getParty(endUserDto.getPartyIds()
                .get(0));

        Long id = endUserRepository.save(endUser)
                .getId();

        Point point = Point.builder()
                .party(party)
                .endUser(endUser)
                .assignedAmount(BigDecimal.ZERO)
                .currentAmount(BigDecimal.ZERO)
                .build();

        pointRepository.save(point);

        log.info("Successfully saved end user with name {} associated with party ID {}", endUserDto.getName(),
                endUserDto.getPartyIds()
                .get(0));

        return id;
    }

    @Transactional
    @Override
    public EndUserDto findEndUserById(Long id) {
        EndUser endUser = getEndUser(id);

        List<Long> partyIds = endUser.getPoints()
                .stream()
                .map(Point::getParty)
                .map(Party::getId)
                .toList();
        List<Long> pointIds = endUser.getPoints()
                .stream()
                .map(Point::getId)
                .toList();

        EndUserDto endUserDto = modelMapper.map(endUser, EndUserDto.class);
        endUserDto.setPartyIds(partyIds);
        endUserDto.setPointIds(pointIds);

        return modelMapper.map(endUser, EndUserDto.class);
    }

    @Override
    public List<EndUserDto> findEndUserById(List<Long> ids) {
        return endUserRepository.findAllById(ids)
                .stream()
                .map(element -> modelMapper.map(element, EndUserDto.class))
                .toList();
    }

    @Override
    public List<EndUserDto> findAllEndUser() {
        List<EndUser> endUserList = endUserRepository.findAll();
        List<EndUserDto> endUserDtoList = new ArrayList<>();

        for (EndUser element : endUserList) {
            EndUserDto map = modelMapper.map(element, EndUserDto.class);
            map.setPartyIds(element.getPoints()
                    .stream()
                    .map(Point::getParty)
                    .map(Party::getId)
                    .toList());
            map.setPointIds(element.getPoints()
                    .stream()
                    .map(Point::getId)
                    .toList());
            endUserDtoList.add(map);
        }

        return endUserDtoList;
    }
    @Override
    public Page<EndUserDto> findAllEndUserByQuery(String name, String partyName, String phone, boolean isActivated,
                                                  Pageable pageable) {
        QEndUser endUser = QEndUser.endUser;
        JPAQuery<EndUser> query = queryFactory.selectFrom(endUser);

        if (name != null) {
            query.where(endUser.name.containsIgnoreCase(name));
        }
        if (partyName != null) {
            QParty party = QParty.party;
            List<Long> partyIds = queryFactory.select(party.id)
                    .from(party)
                    .where(party.name.containsIgnoreCase(partyName))
                    .fetch();

            query.where(endUser.points.any().party.id.in(partyIds));
        }
        if (phone != null) {
            query.where(endUser.phone.contains(phone));
        }

        query.where(endUser.isActivated.eq(isActivated));

        Long count = Objects.requireNonNull(query.clone()
                .select(endUser.count())
                .fetchOne());

        query.orderBy(endUser.id.desc());
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<EndUser> endUserList = query.fetch();

        List<EndUserDto> endUserDtoList = new ArrayList<>();

        for (EndUser element : endUserList) {
            EndUserDto map = modelMapper.map(element, EndUserDto.class);

            // Party ID 리스트 설정
            List<Party> parties = element.getPoints()
                    .stream()
                    .map(Point::getParty)
                    .distinct()
                    .toList();

            map.setPartyIds(parties.stream()
                    .map(Party::getId)
                    .toList());

            // Party 정보 설정 (새로 추가)
            List<PartyDto> partyDtos = parties.stream()
                    .map(party -> modelMapper.map(party, PartyDto.class))
                    .toList();
            map.setParties(partyDtos);

            map.setPointIds(element.getPoints()
                    .stream()
                    .map(Point::getId)
                    .toList());

            endUserDtoList.add(map);
        }

        log.info(endUserDtoList.toString());

        return new PageImpl<>(endUserDtoList, pageable, count);
    }


//    @Override
//    public Page<EndUserDto> findAllEndUserByQuery(String name, String partyName, String phone, boolean isActivated,
//                                                  Pageable pageable) {
//        QEndUser endUser = QEndUser.endUser;
//        JPAQuery<EndUser> query = queryFactory.selectFrom(endUser);
//
//        if (name != null) {
//            query.where(endUser.name.containsIgnoreCase(name));
//        }
//        if (partyName != null) {
//            QParty party = QParty.party;
//            List<Long> partyIds = queryFactory.select(party.id)
//                    .from(party)
//                    .where(party.name.containsIgnoreCase(partyName))
//                    .fetch();
//
//            query.where(endUser.points.any().party.id.in(partyIds));
//        }
//        if (phone != null) {
//            query.where(endUser.phone.contains(phone));
//        }
//
//        query.where(endUser.isActivated.eq(isActivated));
//
//        Long count = Objects.requireNonNull(query.clone()
//                .select(endUser.count())
//                .fetchOne());
//
//        query.orderBy(endUser.id.desc());
//        query.offset(pageable.getOffset());
//        query.limit(pageable.getPageSize());
//
//        List<EndUser> endUserList = query.fetch();
//
//        List<EndUserDto> endUserDtoList = new ArrayList<>();
//
//        for (EndUser element : endUserList) {
//            EndUserDto map = modelMapper.map(element, EndUserDto.class);
//            map.setPartyIds(element.getPoints()
//                    .stream()
//                    .map(Point::getParty)
//                    .map(Party::getId)
//                    .toList());
//            map.setPointIds(element.getPoints()
//                    .stream()
//                    .map(Point::getId)
//                    .toList());
//            endUserDtoList.add(map);
//        }
//
//        log.info(endUserDtoList.toString());
//
//        return new PageImpl<>(endUserDtoList, pageable, count);
//    }

    @Override
    public Page<PointDto> findPointByEndUserId(Long id, Pageable pageable) {
        return pointRepository.findByEndUser_Id(id, pageable)
                .map(element -> modelMapper.map(element, PointDto.class));
    }

    @Transactional
    @Override
    public boolean updateEndUser(EndUserDto endUserDto) {

        EndUser endUser = getEndUser(endUserDto.getId());
        endUser.setPhone(endUserDto.getPhone());
        endUser.setName(endUserDto.getName());

        log.info("partyIds:{}", endUserDto.getPartyIds());
        Set<Party> newParties = new HashSet<>();
        for (Long partyId : endUserDto.getPartyIds()) {
            Party party = partyRepository.findById(partyId)
                    .orElseThrow(() -> new IllegalArgumentException("Party not found"));
            newParties.add(party);
        }

        // 유지할 points와 삭제할 points 식별
        Set<Point> pointsToKeep = new HashSet<>();
        Set<Point> pointsToDelete = new HashSet<>();

        for (Point existingPoint : endUser.getPoints()) {
            if (newParties.contains(existingPoint.getParty())) {
                // 파티가 아직 할당되어 있으므로 이 point 유지
                pointsToKeep.add(existingPoint);
            } else {
                // 더 이상 필요 없는 point는 삭제 대상으로 표시
                pointsToDelete.add(existingPoint);
            }
        }

        // 삭제할 points를 먼저 처리
        for (Point point : pointsToDelete) {

            pointRepository.delete(point);
        }

        // pointRepository.delete 후에 flush를 강제로 수행하여 변경사항을 데이터베이스에 즉시 반영
        pointRepository.flush();

        // 새 파티에 대한 새 points 추가
        for (Party newParty : newParties) {
            // 이 파티에 이미 point가 있는지 확인
            boolean exists = false;
            for (Point point : pointsToKeep) {
                if (point.getParty().equals(newParty)) {
                    exists = true;
                    break;
                }
            }

            // 이 파티에 대한 point가 없으면 새로 생성
            if (!exists) {
                Point newPoint = Point.builder()
                        .endUser(endUser)
                        .party(newParty)
                        .assignedAmount(BigDecimal.ZERO)
                        .currentAmount(BigDecimal.ZERO)
                        .validThru(LocalDateTime.now())
                        .build();
                // 저장하고 유지할 집합에 추가
                Point savedPoint = pointRepository.save(newPoint);
                log.info("save point:{}", savedPoint);
                pointsToKeep.add(savedPoint);
            }
        }

        // 엔드유저의 points 집합을 명시적으로 업데이트
        endUser.getPoints().clear();
        endUser.getPoints().addAll(pointsToKeep);

        // 업데이트된 EndUser 저장
        EndUser savedUser = endUserRepository.save(endUser);

        return savedUser != null;
    }

    @Override
    public boolean updateEndUserPasswordById(Long id, String password) {
        log.info("Updating password for end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setPassword(password);

        endUserRepository.save(endUser);

        log.info("Successfully updated password for end user with ID {}", id);

        return true;
    }

    /**
     * 사용자의 새 비밀번호 업데이트
     *
     * @param id            가맹점 id
     * @param oldPassword   기존 password
     * @param newPassword   새 password
     * @return 성공여부
     */
    @Override
    public boolean updateEndUserPasswordById(Long id, String oldPassword, String newPassword) {
        EndUser endUser = getEndUser(id);
        if (passwordEncoder.matches(oldPassword, endUser.getPassword())) {
            endUser.setPassword(passwordEncoder.encode(newPassword));
            endUserRepository.save(endUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean assignPointToEndUserById(Long endUserId, Long partyId, BigDecimal amount, LocalDateTime validThru) {
        log.info("Assigning point to end user with ID {} associated with party ID {}", endUserId, partyId);

        EndUser endUser = getEndUser(endUserId);
        Party party = getParty(partyId);

        pointRepository.save(Point.builder()
                .party(party)
                .endUser(endUser)
                .currentAmount(amount)
                .validThru(validThru)
                .build());

        log.info("Successfully assigned point to end user with ID {} associated with party ID {}", endUserId, partyId);

        return true;
    }

    @Override
    public boolean activateEndUserById(Long id) {
        log.info("Activating end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setActivated(true);

        endUserRepository.save(endUser);

        log.info("Successfully activated end user with ID {}", id);

        return true;
    }

    @Override
    public List<Boolean> activateEndUserById(List<Long> ids) {
        return ids.stream()
                .map(this::activateEndUserById)
                .toList();
    }

    @Override
    public boolean deactivateEndUserById(Long id) {
        log.info("Deactivating end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setActivated(false);

        endUserRepository.save(endUser);

        log.info("Successfully deactivated end user with ID {}", id);

        return true;
    }

    @Override
    public List<Boolean> deactivateEndUserById(List<Long> ids) {
        return ids.stream()
                .map(this::deactivateEndUserById)
                .toList();
    }
}
