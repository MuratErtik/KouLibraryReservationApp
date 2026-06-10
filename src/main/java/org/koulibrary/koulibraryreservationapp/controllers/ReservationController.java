package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.configs.RestApisConf;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateReservationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.ReservationResponse;
import org.koulibrary.koulibraryreservationapp.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestApisConf.RESERVATIONCONTROLLER)
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateReservationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.create(jwt.getSubject(), request));
    }
}
