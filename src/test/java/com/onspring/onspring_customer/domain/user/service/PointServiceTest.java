package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    private EndUser mockUser;
    private Party mockParty;
    private Point mockPoint;

    @BeforeEach
    void setUp() {
        // Mock 데이터 설정
        mockUser = new EndUser();
        mockUser.setId(30000L);

        mockParty = new Party();
        mockParty.setId(20000L);
        mockParty.setName("Test Party");
        mockParty.setAmount(BigDecimal.valueOf(10000));
        mockParty.setSunday(true);
        mockParty.setMonday(true);
        mockParty.setTuesday(false);
        mockParty.setWednesday(false);
        mockParty.setThursday(true);
        mockParty.setFriday(true);
        mockParty.setSaturday(true);
        mockParty.setActivated(true);
        mockParty.setAllowedTimeStart(LocalTime.parse("09:00"));
        mockParty.setAllowedTimeEnd(LocalTime.parse("18:00"));
        mockPoint = new Point();
        mockPoint.setId(40000L);
        mockPoint.setEndUser(mockUser);
        mockPoint.setParty(mockParty);
        mockPoint.setCurrentAmount(BigDecimal.valueOf(5000));
        mockPoint.setValidThru(LocalDateTime.of(2025, 12, 31, 23, 59));
    }

    @Test
    void getPointsByEndUserId_ShouldReturnPointResponseDtoList() {
        // given
        when(pointRepository.findByEndUserId(30000L)).thenReturn(List.of(mockPoint));

        // when
        List<PointResponseDto> result = pointService.getPointsByEndUserId(30000L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        PointResponseDto dto = result.get(0);

        assertEquals(mockPoint.getCurrentAmount(), dto.getAvailableAmount());
        assertEquals(mockParty.getAmount(), dto.getChargedAmount());
        assertEquals(mockParty.getName(), dto.getPartyName());
        assertEquals(mockParty.isSunday(), dto.isSunday());
        assertEquals(mockParty.isMonday(), dto.isMonday());
        assertEquals(mockParty.isTuesday(), dto.isTuesday());
        assertEquals(mockParty.isWednesday(), dto.isWednesday());
        assertEquals(mockParty.isThursday(), dto.isThursday());
        assertEquals(mockParty.isFriday(), dto.isFriday());
        assertEquals(mockParty.isSaturday(), dto.isSaturday());
        assertEquals(mockParty.isActivated(), dto.isActivated());
        assertEquals(mockParty.getAllowedTimeStart(), dto.getAllowedTimeStart());
        assertEquals(mockParty.getAllowedTimeEnd(), dto.getAllowedTimeEnd());
        assertEquals(mockPoint.getValidThru(), dto.getValidThru());

        // verify
        verify(pointRepository, times(1)).findByEndUserId(30000L);
    }

    @Test
    void getPointsByEndUserId_ShouldReturnEmptyList_WhenNoPointsExist() {
        // given
        when(pointRepository.findByEndUserId(30000L)).thenReturn(Collections.emptyList());

        // when
        List<PointResponseDto> result = pointService.getPointsByEndUserId(30000L);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // verify
        verify(pointRepository, times(1)).findByEndUserId(30000L);
    }

    @Test
    void getPointsByEndUserId_ShouldThrowException_WhenEndUserIdIsNull() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.getPointsByEndUserId(null);
        });

        assertEquals("endUserId cannot be null", exception.getMessage());
    }
}