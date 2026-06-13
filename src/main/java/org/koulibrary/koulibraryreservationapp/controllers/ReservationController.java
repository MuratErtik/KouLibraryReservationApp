package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.configs.RestApisConf;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CancelReservationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CheckInRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateReservationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.AdminReservationResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.MyReservationResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.ReservationResponse;
import org.koulibrary.koulibraryreservationapp.services.ReservationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/me")
    public ResponseEntity<PageResponse<MyReservationResponse>> getMyReservations(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10, sort = "reservationTime", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reservationService.getMyReservations(jwt.getSubject(), pageable));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<MyReservationResponse> cancel(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody(required = false) CancelReservationRequest request) {

        return ResponseEntity.ok(reservationService.cancel(jwt.getSubject(), id, request));
    }

    @PostMapping("/check-in")
    public ResponseEntity<MyReservationResponse> checkIn(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CheckInRequest request) {

        return ResponseEntity.ok(reservationService.checkIn(jwt.getSubject(), request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<MyReservationResponse> complete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.complete(jwt.getSubject(), id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdminReservationResponse>> getAllForAdmin(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long deskId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reservationService.getAllForAdmin(status, userId, deskId, date, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminReservationResponse> getByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getByIdForAdmin(id));
    }

    @PatchMapping("/{id}/admin-cancel")
    public ResponseEntity<AdminReservationResponse> adminCancel(
            @PathVariable Long id,
            @RequestBody(required = false) CancelReservationRequest request) {

        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(reservationService.adminCancel(id, reason));
    }
}
