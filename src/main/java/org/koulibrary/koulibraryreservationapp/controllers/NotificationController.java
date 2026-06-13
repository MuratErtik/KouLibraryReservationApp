package org.koulibrary.koulibraryreservationapp.controllers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.services.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.NOTIFICATIONCONTROLLER;

@RestController
@RequestMapping(NOTIFICATIONCONTROLLER)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<PageResponse<NotificationResponse>> getMine(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMyNotifications(jwt.getSubject(), pageable));
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of("unread", notificationService.getUnreadCount(jwt.getSubject())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        notificationService.markRead(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }
}
