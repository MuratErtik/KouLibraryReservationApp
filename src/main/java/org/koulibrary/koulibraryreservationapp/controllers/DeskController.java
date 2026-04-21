package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.services.DeskService;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.DESKCONTROLLER;

@RestController
@RequestMapping(DESKCONTROLLER)
@RequiredArgsConstructor
public class DeskController {

    private final DeskService deskService;

    @PostMapping
    public ResponseEntity<CreateDeskResponse> createSaloon(@Valid @RequestBody CreateDeskRequest request,
                                                           @PathVariable Long libraryId, @PathVariable Long saloonId) {

        CreateDeskResponse response = deskService.createDesk(request,libraryId,saloonId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping("/{deskId}")
    public ResponseEntity<DeskResponse> updateDesk(
            @PathVariable Long deskId,
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @Valid @RequestBody UpdateDeskRequest request) {

        DeskResponse response = deskService.updateDesk(libraryId,saloonId,deskId,request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{deskId}")
    public ResponseEntity<DeskResponse> getDeskById(@PathVariable Long libraryId, @PathVariable Long saloonId, @PathVariable Long deskId) {

        return ResponseEntity.ok(deskService.getDeskById(libraryId,saloonId,deskId));
    }


    // pagination for admin's future abilities.
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<DeskResponse>> getAllDesk(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @PageableDefault(size = 10, sort = "deskNumber") Pageable pageable) {

        return ResponseEntity.ok(deskService.getAllDesks(pageable,saloonId,libraryId));
    }

    // not pagination for user's future abilities.
    @GetMapping("/user")
    public ResponseEntity<Set<DeskResponse>> getAllDeskWithoutPagination(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId) {
        return ResponseEntity.ok(deskService.getAllDeskWithoutPagination(saloonId,libraryId));
    }


    // pagination for admin's future abilities.
    @GetMapping("/admin/filter")
    public ResponseEntity<PageResponse<DeskResponse>> getAllDeskByFilter(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @RequestParam(required = false) DeskPolicy deskPolicy,
            @RequestParam(required = false) DeskStatus deskStatus,
            @RequestParam(required = false) Boolean hasPowerSocket,
            @PageableDefault(size = 10, sort = "deskNumber") Pageable pageable) {

        return ResponseEntity.ok(deskService.getAllDeskByFilter(pageable,saloonId,libraryId,deskPolicy,deskStatus,hasPowerSocket));
    }

    // not pagination for user's future abilities.
    @GetMapping("/user/filter")
    public ResponseEntity<Set<DeskResponse>> getAllDeskByFilterWithoutPagination(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @RequestParam(required = false) DeskPolicy deskPolicy,
            @RequestParam(required = false) DeskStatus deskStatus,
            @RequestParam(required = false) Boolean hasPowerSocket) {
        return ResponseEntity.ok(deskService.getAllDeskByFilterWithoutPagination(saloonId,libraryId,deskPolicy,deskStatus,hasPowerSocket));
    }

    @DeleteMapping("/{deskId}")
    public ResponseEntity<Void> deleteDesk(@PathVariable Long libraryId, @PathVariable Long saloonId, @PathVariable Long deskId) {


        deskService.deleteDesk(libraryId,saloonId,deskId);
        return ResponseEntity.noContent().build();


    }





}
