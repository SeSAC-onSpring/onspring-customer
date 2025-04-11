package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/view/users")
public class UserViewController {
    private final EndUserService endUserService;
    private final PartyService partyService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/add")
    String showSaveUser(Model model) {
        List<PartyDto> partyList = partyService.findAllParty();

        model.addAttribute("parties", partyList);

        return "users/add";
    }

    @PostMapping("/add")
    String saveUser(@RequestParam(value = "name") String name, @RequestParam(value = "phone") String phone,
                    @RequestParam(value = "password") String password, @RequestParam(value = "partyId") Long partyId) {
        EndUserDto endUserDto = new EndUserDto();
        endUserDto.setName(name);
        endUserDto.setPhone(phone);
        endUserDto.setPartyIds(Collections.singletonList(partyId));
        endUserDto.setPassword(passwordEncoder.encode(password));
        endUserDto.setActivated(true);

        endUserService.saveEndUser(endUserDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getUsers(@RequestParam(value = "searchType", required = false) String searchType, @RequestParam(value =
            "keyword", required = false) String keyword,
                    @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size",
                    defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserDto> endUserDtoPage;
        if (searchType == null) {
            endUserDtoPage = endUserService.findAllEndUserByQuery(null, null, null, true, pageable);
        } else {
            endUserDtoPage = switch (searchType) {
                case "name" -> endUserService.findAllEndUserByQuery(keyword, null, null, true, pageable);
                case "partyName" -> endUserService.findAllEndUserByQuery(null, keyword, null, true, pageable);
                case "phone" -> endUserService.findAllEndUserByQuery(null, null, keyword, true, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        }

        model.addAttribute("users", endUserDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserDtoPage.getTotalPages());

        return "users/list";
    }

    @GetMapping("/activate")
    String getDeactivatedUsers(@RequestParam(value = "searchType", required = false) String searchType,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value =
                    "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserDto> endUserDtoPage;
        if (searchType == null) {
            endUserDtoPage = endUserService.findAllEndUserByQuery(null, null, null, false, pageable);
        } else {
            endUserDtoPage = switch (searchType) {
                case "name" -> endUserService.findAllEndUserByQuery(keyword, null, null, false, pageable);
                case "partyName" -> endUserService.findAllEndUserByQuery(null, keyword, null, false, pageable);
                case "phone" -> endUserService.findAllEndUserByQuery(null, null, keyword, false, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        }

        model.addAttribute("users", endUserDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserDtoPage.getTotalPages());

        return "users/activate";
    }
    @GetMapping("/edit/{id}")
    String showEditForm(@PathVariable Long id, Model model) {
        // 회원 정보 가져오기
        EndUserDto user = endUserService.findEndUserById(id);

        // 그룹 목록 가져오기
        List<PartyDto> parties = partyService.findAllParty();


        // 디버깅 로그 추가
        System.out.println("User: " + user);
        System.out.println("Parties size: " + (parties != null ? parties.size() : "null"));
        if(parties != null && !parties.isEmpty()) {
            System.out.println("First party: " + parties.get(0).getName());
        }

        model.addAttribute("user", user);
        model.addAttribute("parties", parties);

        return "users/edit";
    }

    @PostMapping("/edit/{id}")
    String updateUser(@PathVariable Long id,
                      @RequestParam(value = "name") String name,
                      @RequestParam(value = "phone") String phone,
                      @RequestParam(value = "partyIds", required = false) List<Long> partyIds) {

        // 기존 회원 정보 가져오기
        EndUserDto userDto = endUserService.findEndUserById(id);
        // 정보 업데이트
        userDto.setName(name);
        userDto.setPhone(phone);

        log.info("partyIds:" + partyIds);

        // partyIds가 null인 경우 빈 리스트로 설정
        if (partyIds == null) {
            partyIds = new ArrayList<>();
        }

        userDto.setPartyIds(partyIds);


        // 업데이트 처리
        endUserService.updateEndUser(userDto);

        return "redirect:/view/users/list";
    }

}