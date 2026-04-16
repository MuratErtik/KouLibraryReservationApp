package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.services.DeskService;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PatchMapping("/update-desk/{deskId}")
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






}
