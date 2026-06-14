package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.JoinWaitlistRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.WaitlistResponse;
import org.koulibrary.koulibraryreservationapp.services.WaitlistService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.WAITLISTCONTROLLER;

@RestController
@RequestMapping(WAITLISTCONTROLLER) // /dev/v1/waitlist
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    @PostMapping
    public ResponseEntity<WaitlistResponse> join(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody JoinWaitlistRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waitlistService.join(jwt.getSubject(), req.slotId()));
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<WaitlistResponse>> getMine(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "requestedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(waitlistService.getMine(jwt.getSubject(), pageable));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        waitlistService.cancel(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }
}
