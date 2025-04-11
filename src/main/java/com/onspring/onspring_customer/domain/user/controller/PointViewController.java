package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.EndUserPointDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.domain.user.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/points")
public class PointViewController {
    private final PartyService partyService;
    private final EndUserService endUserService;
    private final PointService pointService;

    @GetMapping("/issue")
    public String getParty(@RequestParam(value = "searchType", required = false) String searchType,
                           @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value =
                    "page", defaultValue = "1") Integer page,
                           @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyDto> partyDtoPage;
        if (searchType != null) {
            partyDtoPage = switch (searchType) {
                case "party" ->
                        partyService.findAllPartyByQuery(keyword, null, null, false, false, false, false, false,
                                false, false, null, null, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        } else {
            partyDtoPage = partyService.findAllPartyByQuery(null, null, null, false, false, false, false, false,
                    false, false, null, null, pageable);
        }

        model.addAttribute("parties", partyDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyDtoPage.getTotalPages());

        return "points/issue";
    }

    @GetMapping("/add/{partyId}")
    public String getUsersAndPoints(@PathVariable("partyId") Long partyId, @RequestParam(value = "page",
            defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                                    Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserPointDto> endUserPointDtoPage = pointService.findAllEndUserAndPointByPartyId(partyId, pageable);

        model.addAttribute("models", endUserPointDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserPointDtoPage.getTotalPages());

        return "points/point_add_member";
    }

    @GetMapping("/add/{partyId}/point")
    public String assignPointToUsers(@PathVariable("partyId") Long partyId,
                                     @RequestParam(value = "userIds") List<Long> userIds, Model model) {
        PartyDto partyDto = partyService.findPartyById(partyId);
        List<EndUserDto> endUserDtoList = endUserService.findEndUserById(userIds);

        model.addAttribute("party", partyDto);
        model.addAttribute("endUsers", endUserDtoList);

        return "points/point_add_member2";
    }

    @PostMapping("/add/{partyId}/point")
    public String assignPointToUsers(@PathVariable("partyId") Long partyId,
                                     @RequestParam(value = "userIds") List<Long> userIds, @RequestParam(value =
                    "amount") BigDecimal amount, @RequestParam(value = "validThru") LocalDateTime validThru) {
        pointService.assignPointToEndUserById(userIds, partyId, amount, validThru);

        return "redirect:../{partyId}";
    }

    @GetMapping("/modify")
    public String getPartyModify(@RequestParam(value = "searchType", required = false) String searchType,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value
                    = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyDto> partyDtoPage;
        if (searchType != null) {
            partyDtoPage = switch (searchType) {
                case "party" ->
                        partyService.findAllPartyByQuery(keyword, null, null, false, false, false, false, false,
                                false, false, null, null, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        } else {
            partyDtoPage = partyService.findAllPartyByQuery(null, null, null, false, false, false, false, false,
                    false, false, null, null, pageable);
        }

        model.addAttribute("parties", partyDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyDtoPage.getTotalPages());

        return "points/modify";
    }

    @GetMapping("/modify/{partyId}")
    public String getUsersAndPointsModify(@PathVariable("partyId") Long partyId, @RequestParam(value = "page",
            defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserPointDto> endUserPointDtoPage = pointService.findAllEndUserAndPointByPartyId(partyId, pageable);

        model.addAttribute("models", endUserPointDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserPointDtoPage.getTotalPages());

        return "points/point_edit_member";
    }

    @GetMapping("/modify/{partyId}/point")
    public String assignPointToUsersModify(@PathVariable("partyId") Long partyId,
                                           @RequestParam(value = "userId") Long userId, Model model) {
        EndUserPointDto endUserPointDto = pointService.findEndUserAndPointByPartyIdAndEndUserId(partyId, userId);

        model.addAttribute("point", endUserPointDto);

        return "points/point_edit_member2";
    }

    @PostMapping("/modify/{partyId}/point")
    public String assignPointToUsersModify(@PathVariable("partyId") Long partyId,
                                           @RequestParam(value = "userId") Long userId, @RequestParam(value = "amount"
            ) BigDecimal amount, @RequestParam(value = "validThru") LocalDateTime validThru) {
        pointService.updatePointOfEndUserById(userId, partyId, amount, validThru);

        return "redirect:../{partyId}";
    }
}
