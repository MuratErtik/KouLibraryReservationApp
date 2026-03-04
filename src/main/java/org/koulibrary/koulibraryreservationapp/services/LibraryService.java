package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateLibraryResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;

import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryManager libraryManager;

    public CreateLibraryResponse createLibrary(@Valid CreateLibraryRequest request) {

        Library library = mapToEntity(request);

        Library savedLibrary = libraryManager.saveLibrary(library);

        return CreateLibraryResponse.builder()
                .id(savedLibrary.getId())
                .message(String.format("Library '%s' created", savedLibrary.getName()))

                .build();
    }

    private Library mapToEntity(CreateLibraryRequest request) {
        return Library.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .checkpointGraceMinutes(request.getCheckpointGraceMinutes())
                .maxActiveReservationsPerUser(request.getMaxActiveReservationsPerUser())
                .reservationWindowInDays(request.getReservationWindowInDays())
                .checkInTimeoutMinutes(request.getCheckInTimeoutMinutes())
                .checkpointIntervalMinutes(request.getCheckpointIntervalMinutes())
                .penaltyBlockDays(request.getPenaltyBlockDays())
                .build();
    }


}
