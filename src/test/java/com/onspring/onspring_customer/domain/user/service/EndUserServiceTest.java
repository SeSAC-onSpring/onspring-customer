package com.onspring.onspring_customer.domain.user.service;


import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndUserServiceTest {

    @Mock
    private EndUserRepository endUserRepository;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EndUserServiceImpl endUserService;

    private EndUserDto endUserDto;
    private EndUser endUser;
    private Party party;
    private Point point;

    @BeforeEach
    void setUp() {
        party = Party.builder()
                .id(1L)
                .build();

        endUserDto = new EndUserDto(1L, 1L, "password", "user", "0123456789", true, new BigDecimal(0));

        point = Point.builder()
                .id(1L)
                .party(party)
                .build();

        endUser = EndUser.builder()
                .id(1L)
                .password("password")
                .phone("0123456789")
                .isActivated(true)
                .build();

        point.setEndUser(endUser);
    }

    @Test
    void testSaveEndUser() {
        when(modelMapper.map(any(EndUserDto.class), any())).thenReturn(endUser);
        when(partyRepository.findById(endUserDto.getPartyIds()
                .get(Math.toIntExact(party.getId())))).thenReturn(Optional.of(party));
        when(endUserRepository.save(any(EndUser.class))).thenReturn(endUser);

        endUserService.saveEndUser(endUserDto);

        verify(endUserRepository).save(endUser);
        verify(partyRepository).findById(endUserDto.getPartyIds()
                .get(Math.toIntExact(party.getId())));
        verify(pointRepository).save(any(Point.class));
    }

    @Test
    void testFindEndUserById() {

        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));
        when(modelMapper.map(any(EndUser.class), any())).thenReturn(endUserDto);

        EndUserDto endUserDto1 = endUserService.findEndUserById(1L);

        assertNotNull(endUserDto1);
        assertEquals(endUserDto.getId(), endUserDto1.getId());
    }

    @Test
    void testFindEndUserById_NotFound() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> endUserService.findEndUserById(1L));
    }

    @Test
    void testUpdateEndUserPasswordById() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.updateEndUserPasswordById(1L, "password1");

        assertTrue(result);

        verify(endUserRepository).save(endUser);

        assertEquals("password1", endUser.getPassword());
    }

    @Test
    void testActivateEndUserById() {
        endUser.setActivated(false);
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.activateEndUserById(1L);

        assertTrue(result);

        verify(endUserRepository).save(endUser);

        assertTrue(endUser.isActivated());
    }

    @Test
    void testDeactivateEndUserById() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.deactivateEndUserById(1L);

        assertTrue(result);

        verify(endUserRepository).save(endUser);

        assertFalse(endUser.isActivated());
    }
}

