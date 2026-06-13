package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreatePenaltyRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdatePenaltyDurationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PenaltyResponse;
import org.koulibrary.koulibraryreservationapp.services.PenaltyService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.PENALTYCONTROLLER;

@RestController
@RequestMapping(PENALTYCONTROLLER)
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping("/me")
    public ResponseEntity<PageResponse<PenaltyResponse>> getMyPenalties(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(penaltyService.getMyPenalties(jwt.getSubject(), pageable));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PenaltyResponse>> getAllForAdmin(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) PenaltyStatus status,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(penaltyService.getAllForAdmin(userId, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyResponse> getByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(penaltyService.getByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<PenaltyResponse> create(@Valid @RequestBody CreatePenaltyRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(penaltyService.createManual(req));
    }

    @PatchMapping("/{id}/duration")
    public ResponseEntity<PenaltyResponse> updateDuration(
            @PathVariable Long id, @Valid @RequestBody UpdatePenaltyDurationRequest req) {
        return ResponseEntity.ok(penaltyService.updateDuration(id, req.getDays()));
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<PenaltyResponse> revoke(@PathVariable Long id) {
        return ResponseEntity.ok(penaltyService.revoke(id));
    }
}
