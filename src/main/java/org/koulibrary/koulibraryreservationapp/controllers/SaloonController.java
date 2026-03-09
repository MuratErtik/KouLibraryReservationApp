package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateSaloonResponse;
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





}
