package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.EndUserPointDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PointServiceImpl implements PointService {
    private final PartyRepository partyRepository;
    private final EndUserRepository endUserRepository;
    private final PointRepository pointRepository;
    private final ModelMapper modelMapper;

    private EndUser getEndUser(Long id) {
        Optional<EndUser> result = endUserRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("EndUser with ID " + id + " not found"));
    }

    private Party getParty(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    private EndUserPointDto createEndUserPointDto(EndUser endUser, Party party, Long partyId) {
        PointDto pointDto = endUser.getPoints()
                .stream()
                .filter(point -> Objects.equals(point.getParty()
                        .getId(), partyId))
                .findFirst()
                .map(point -> modelMapper.map(point, PointDto.class))
                .orElseGet(() -> createDefaultPointDto(party, endUser));

        return new EndUserPointDto(modelMapper.map(endUser, EndUserDto.class), pointDto);
    }

    private PointDto createDefaultPointDto(Party party, EndUser endUser) {
        return modelMapper.map(Point.builder()
                .party(party)
                .endUser(endUser)
                .assignedAmount(BigDecimal.ZERO)
                .currentAmount(BigDecimal.ZERO)
                .build(), PointDto.class);
    }

    /**
     *
     * @param endUserId 포인트를 조회할 사용자의 Id
     * @return Id에 해당하는 사용자의 그룹 별 포인트 내역, 정책
     */
    @Override
    public List<PointResponseDto> getPointsByEndUserId(Long endUserId) {
        if (endUserId == null) {
            throw new IllegalArgumentException("endUserId cannot be null");
        }
        List<Point> pointList = pointRepository.findByEndUserId(endUserId);

        // pointList가 null일 수 있으므로 체크
        if (pointList == null || pointList.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        return pointList.stream()
                .map(point -> {
                    Party party = point.getParty();

                    return new PointResponseDto(
                            point.getId(),
                            party.getId(),
                            point.getCurrentAmount(),          // 사용 가능한 포인트
                            point.getAssignedAmount(),            // 충전된 포인트 (추가 로직 필요)
                            party.getName(),            // 파티명
                            party.isSunday(),
                            party.isMonday(),
                            party.isTuesday(),
                            party.isWednesday(),
                            party.isThursday(),
                            party.isFriday(),
                            party.isSaturday(),
                            party.isActivated(),
                            party.getMaximumTransaction(),
                            party.getMaximumAmount(),
                            party.getCreatedAt(),
                            party.getAllowedTimeStart(),
                            party.getAllowedTimeEnd(),
                            point.getValidThru()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 결제 시 사용자의 포인트에서 금액만킄 차감
     * @param pointId 해당 포인트의 Id
     * @param amount  차감할 포인트
     * @return 성공여부
     */
    @Override
    public boolean usePointOnPayment(Long pointId, BigDecimal amount) {
        if (pointId == null) {
            throw new IllegalArgumentException("pointId cannot be null");
        }
        Point point = pointRepository.findById(pointId).orElse(null);

        point.setCurrentAmount(point.getCurrentAmount().subtract(amount));

        pointRepository.save(point);

        return true;
    }

    @Override
    public boolean assignPointToEndUserById(Long endUserId, Long partyId, BigDecimal amount, LocalDateTime validThru) {
        return false;
    }

    @Override
    public boolean assignPointToEndUserById(List<Long> endUserIds, Long partyId, BigDecimal amount,
                                            LocalDateTime validThru) {
        log.info("Assigning point to end users with ID {} associated with party ID {}", endUserIds, partyId);

        List<Point> pointList = pointRepository.findByParty_IdAndEndUser_IdIn(partyId, endUserIds);

        pointList.forEach(point -> {
            point.setAssignedAmount(point.getCurrentAmount()
                    .add(amount));
            point.setCurrentAmount(point.getCurrentAmount()
                    .add(amount));
            point.setValidThru(validThru);
        });

        pointRepository.saveAll(pointList);

        log.info("Successfully assigned point to end users with ID {} associated with party ID {}", endUserIds,
                partyId);

        return true;
    }

    @Override
    public boolean updatePointOfEndUserById(Long endUserId, Long partyId, BigDecimal amount, LocalDateTime validThru) {
        log.info("Updating point to end user with ID {} associated with party ID {}", endUserId, partyId);

        Point point = pointRepository.findByParty_IdAndEndUser_Id(partyId, endUserId).orElseThrow();

        point.setAssignedAmount(amount);
        point.setCurrentAmount(amount);
        point.setValidThru(validThru);

        pointRepository.save(point);

        log.info("Successfully updated point to end user with ID {} associated with party ID {}", endUserId, partyId);

        return true;
    }

    @Override
    public boolean updatePointOfEndUserById(List<Long> endUserIds, Long partyId, BigDecimal amount,
                                            LocalDateTime validThru) {
        log.info("Updating point to end users with ID {} associated with party ID {}", endUserIds, partyId);

        List<Point> pointList = pointRepository.findByParty_IdAndEndUser_IdIn(partyId, endUserIds);

        pointList.forEach(point -> {
            point.setAssignedAmount(amount);
            point.setCurrentAmount(amount);
            point.setValidThru(validThru);
        });

        pointRepository.saveAll(pointList);

        log.info("Successfully updated point to end users with ID {} associated with party ID {}", endUserIds, partyId);

        return true;
    }

    @Override
    public PointDto findAvailablePointByEndUserIdAndPartyId(Long endUserId, Long partyId) {
        return null;
    }

    @Override
    public EndUserPointDto findEndUserAndPointByPartyIdAndEndUserId(Long partyId, Long endUserId) {
        Point point = pointRepository.findByParty_IdAndEndUser_Id(partyId, endUserId)
                .orElseThrow();
        return new EndUserPointDto(modelMapper.map(point.getEndUser(), EndUserDto.class), modelMapper.map(point,
                PointDto.class));
    }

    @Override
    public Page<EndUserPointDto> findAllEndUserAndPointByPartyId(Long id, Pageable pageable) {
        Party party = getParty(id);

        List<EndUserPointDto> endUserPointDtoList = party.getPoints()
                .stream()
                .map(Point::getEndUser)
                .map(endUser -> createEndUserPointDto(endUser, party, id))
                .toList();

        return new PageImpl<>(endUserPointDtoList, pageable, endUserPointDtoList.size());
    }
}
