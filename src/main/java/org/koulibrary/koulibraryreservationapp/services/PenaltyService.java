package org.koulibrary.koulibraryreservationapp.services;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.LibraryStatus;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyReason;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.exceptions.*;

import org.koulibrary.koulibraryreservationapp.repositories.PenaltyRepository;

import org.koulibrary.koulibraryreservationapp.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;


@Service
@RequiredArgsConstructor
public class PenaltyService {

    public static final List<PenaltyStatus> BLOCKING = List.of(PenaltyStatus.ACTIVE, PenaltyStatus.APPEALED);

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    @Transactional
    public Penalty createPenalty(User user, Reservation reservation, PenaltyReason reason, int days, String description) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        Penalty penalty = Penalty.builder()
                .user(user)
                .reservation(reservation)          // null = manuel
                .reason(reason)
                .status(PenaltyStatus.ACTIVE)
                .startTime(now)
                .endTime(now.plusDays(days))
                .description(description)
                .build();
        return penaltyRepository.save(penalty);
    }

    @Transactional
    public PenaltyResponse createManual(CreatePenaltyRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + req.getUserId()));
        Penalty p = createPenalty(user, null, PenaltyReason.MANUAL_ADMIN, req.getDays(), req.getDescription());
        return toResponse(p, true);
    }

    @Transactional
    public PenaltyResponse updateDuration(Long id, int days) {
        Penalty p = penaltyRepository.findByIdWithUser(id)
                .orElseThrow(() -> new PenaltyNotFoundException("Penalty not found with ID: " + id));
        if (p.getStatus() == PenaltyStatus.REVOKED || p.getStatus() == PenaltyStatus.EXPIRED) {
            throw new PenaltyOperationException("Cannot change duration of an inactive penalty: " + p.getStatus());
        }
        p.setEndTime(p.getStartTime().plusDays(days));
        return toResponse(p, true);
    }

    @Transactional
    public PenaltyResponse revoke(Long id) {
        Penalty p = penaltyRepository.findByIdWithUser(id)
                .orElseThrow(() -> new PenaltyNotFoundException("Penalty not found with ID: " + id));
        if (p.getStatus() == PenaltyStatus.REVOKED || p.getStatus() == PenaltyStatus.EXPIRED) {
            throw new PenaltyOperationException("Penalty is already inactive: " + p.getStatus());
        }
        p.setStatus(PenaltyStatus.REVOKED);
        return toResponse(p, true);
    }

    @Transactional(readOnly = true)
    public PageResponse<PenaltyResponse> getMyPenalties(String keycloakSub, Pageable pageable) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        Page<Penalty> page = penaltyRepository.findByUserId(user.getId(), pageable);
        return toPage(page, false);
    }

    @Transactional(readOnly = true)
    public PageResponse<PenaltyResponse> getAllForAdmin(Long userId, PenaltyStatus status, Pageable pageable) {
        return toPage(penaltyRepository.findForAdmin(userId, status, pageable), true);
    }

    @Transactional(readOnly = true)
    public PenaltyResponse getByIdForAdmin(Long id) {
        Penalty p = penaltyRepository.findByIdWithUser(id)
                .orElseThrow(() -> new PenaltyNotFoundException("Penalty not found with ID: " + id));
        return toResponse(p, true);
    }

    @Transactional
    public int expirePenalties() {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        List<Penalty> list = penaltyRepository.findByStatusAndEndTimeLessThanEqual(PenaltyStatus.ACTIVE, now);
        list.forEach(p -> p.setStatus(PenaltyStatus.EXPIRED));
        return list.size();
    }

    private PageResponse<PenaltyResponse> toPage(Page<Penalty> page, boolean includeUser) {
        List<PenaltyResponse> content = page.getContent().stream().map(p -> toResponse(p, includeUser)).toList();
        return PageResponse.<PenaltyResponse>builder()
                .content(content).pageNumber(page.getNumber()).pageSize(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).isLast(page.isLast())
                .build();
    }

    private PenaltyResponse toResponse(Penalty p, boolean includeUser) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        boolean active = (p.getStatus() == PenaltyStatus.ACTIVE || p.getStatus() == PenaltyStatus.APPEALED)
                && p.getEndTime().isAfter(now);

        PenaltyResponse.PenaltyResponseBuilder b = PenaltyResponse.builder()
                .id(p.getId())
                .reason(p.getReason())
                .status(p.getStatus())
                .startTime(p.getStartTime())
                .endTime(p.getEndTime())
                .description(p.getDescription())
                .reservationId(p.getReservation() != null ? p.getReservation().getId() : null)
                .active(active);

        if (includeUser) {
            User u = p.getUser();
            b.userId(u.getId()).studentIdNumber(u.getStudentIdNumber())
                    .userFullName(u.getFirstName() + " " + u.getLastName());
        }
        return b.build();
    }
}
