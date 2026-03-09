package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateSaloonResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryClosureResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SaloonResponse;
import org.koulibrary.koulibraryreservationapp.services.SaloonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.SALOONCONTROLLER;

@RestController
@RequestMapping(SALOONCONTROLLER)
@RequiredArgsConstructor
public class SaloonController {

    private final SaloonService saloonService;



    @PostMapping
    public ResponseEntity<CreateSaloonResponse> createSaloon(@Valid @RequestBody CreateSaloonRequest request, @PathVariable Long libraryId) {

        CreateSaloonResponse response = saloonService.createSaloon(request,libraryId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/update-saloon/{saloonId}")
    public ResponseEntity<SaloonResponse> updateSaloon(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @Valid @RequestBody UpdateSaloonRequest request) {

        SaloonResponse response = saloonService.updateSaloon(libraryId,saloonId,request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





}
