package com.onspring.onspring_customer.domain.customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/view/parties")
public class PartyViewController {
    private final PartyService partyService;

    @Autowired
    public PartyViewController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping("/add")
    public String showSaveParty() {
        return "parties/add";
    }

    @PostMapping("/add")
    public String saveParty(@RequestParam(value = "customerId") Long customerId,
                            @RequestParam(value = "name") String name,
                            @RequestParam(value = "period") LocalDateTime period,
                            @RequestParam(value = "amount") BigDecimal amount, @RequestParam(value =
                    "allowedTimeStart") LocalTime allowedTimeStart,
                            @RequestParam(value = "allowedTimeEnd") LocalTime allowedTimeEnd, @RequestParam(value =
                    "validThru") Long validThru,
                            @RequestParam(value = "sunday", defaultValue = "false") Boolean sunday,
                            @RequestParam(value = "monday", defaultValue = "false") Boolean monday,
                            @RequestParam(value = "tuesday", defaultValue = "false") Boolean tuesday,
                            @RequestParam(value = "wednesday", defaultValue = "false") Boolean wednesday,
                            @RequestParam(value = "thursday", defaultValue = "false") Boolean thursday,
                            @RequestParam(value = "friday", defaultValue = "false") Boolean friday,
                            @RequestParam(value = "saturday", defaultValue = "false") Boolean saturday,
                            @RequestParam(value = "maximumAmount") BigDecimal maximumAmount,
                            @RequestParam(value = "maximumTransaction") Long maximumTransaction) {
        PartyDto partyDto = new PartyDto(null, customerId, name, period, amount, allowedTimeStart, allowedTimeEnd,
                validThru, sunday, monday, tuesday, wednesday, thursday, friday, saturday, maximumAmount,
                maximumTransaction, true, null);
        partyService.saveParty(partyDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    public String getParties(@RequestParam(value = "customerId", required = false) Long customerId,
                             @RequestParam(value = "name", required = false) String name, @RequestParam(value =
                    "period", required = false) LocalDateTime period, @RequestParam(value = "amount", required =
                    false) BigDecimal amount,
                             @RequestParam(value = "allowedTimeStart", required = false) LocalTime allowedTimeStart,
                             @RequestParam(value = "allowedTimeEnd", required = false) LocalTime allowedTimeEnd,
                             @RequestParam(value = "validThru", required = false) Long validThru,
                             @RequestParam(value = "sunday", defaultValue = "false") Boolean sunday,
                             @RequestParam(value = "monday", defaultValue = "false") Boolean monday,
                             @RequestParam(value = "tuesday", defaultValue = "false") Boolean tuesday,
                             @RequestParam(value = "wednesday", defaultValue = "false") Boolean wednesday,
                             @RequestParam(value = "thursday", defaultValue = "false") Boolean thursday,
                             @RequestParam(value = "friday", defaultValue = "false") Boolean friday,
                             @RequestParam(value = "saturday", defaultValue = "false") Boolean saturday,
                             @RequestParam(value = "maximumAmount", required = false) BigDecimal maximumAmount,
                             @RequestParam(value = "maximumTransaction", required = false) Long maximumTransaction,
                             @RequestParam(value = "showDeactivated", defaultValue = "true", required = false) Boolean showDeactivated, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyDto> partyDtoPage = partyService.findAllPartyByQuery(name, allowedTimeStart, allowedTimeEnd, sunday
                , monday, tuesday, wednesday, thursday, friday, saturday, maximumAmount, maximumTransaction, pageable);

        model.addAttribute("parties", partyDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyDtoPage.getTotalPages());

        return "parties/list";
    }

    @PutMapping("/update")
    public String updateParty(@RequestParam(value = "id") Long id, @RequestParam(value = "name") String name,
                              @RequestParam(value = "period") LocalDateTime period,
                              @RequestParam(value = "amount") BigDecimal amount, @RequestParam(value =
                    "allowedTimeStart") LocalTime allowedTimeStart,
                              @RequestParam(value = "allowedTimeEnd") LocalTime allowedTimeEnd, @RequestParam(value =
                    "validThru") Long validThru, @RequestParam(value = "sunday") Boolean sunday, @RequestParam(value
                    = "monday") Boolean monday, @RequestParam(value = "tuesday") Boolean tuesday,
                              @RequestParam(value = "wednesday") Boolean wednesday,
                              @RequestParam(value = "thursday") Boolean thursday,
                              @RequestParam(value = "friday") Boolean friday,
                              @RequestParam(value = "saturday") Boolean saturday, @RequestParam(value =
                    "maximumAmount") BigDecimal maximumAmount,
                              @RequestParam(value = "maximumTransaction") Long maximumTransaction) {
        PartyDto partyDto = PartyDto.builder()
                .id(id)
                .name(name)
                .period(period)
                .amount(amount)
                .allowedTimeStart(allowedTimeStart)
                .allowedTimeEnd(allowedTimeEnd)
                .validThru(validThru)
                .sunday(sunday)
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .saturday(saturday)
                .maximumAmount(maximumAmount)
                .maximumTransaction(maximumTransaction)
                .build();
        partyService.updateParty(partyDto);

        return "redirect:list";
    }

    @PatchMapping("/activate")
    public String activateParty(@RequestParam(value = "id") Long id) {
        partyService.activatePartyById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    public String deactivateParty(@RequestParam(value = "id") Long id) {
        partyService.deactivatePartyById(id);

        return "redirect:list";
    }
}
