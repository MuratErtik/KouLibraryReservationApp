package org.koulibrary.koulibraryreservationapp.controllers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.responses.DeskAvailabilityResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SlotResponse;
import org.koulibrary.koulibraryreservationapp.services.LibraryTimeSlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.LIBRARYTIMESLOTCONTROLLER;

@RestController
@RequestMapping(LIBRARYTIMESLOTCONTROLLER)
@RequiredArgsConstructor
public class LibraryTimeSlotController {

    private final LibraryTimeSlotService libraryTimeSlotService;

    @GetMapping()
    public ResponseEntity<List<SlotResponse>> getSlots(
            @PathVariable Long saloonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(libraryTimeSlotService.getSlots(saloonId, date));
    }

    @GetMapping("/{slotId}/available-desks")
    public ResponseEntity<List<DeskAvailabilityResponse>> getAvailableDesks(
            @PathVariable Long saloonId,
            @PathVariable Long slotId) {

        return ResponseEntity.ok(libraryTimeSlotService.getAvailableDesks(saloonId, slotId));
    }


}

