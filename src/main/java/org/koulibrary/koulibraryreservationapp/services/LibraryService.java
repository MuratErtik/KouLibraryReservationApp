package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateLibraryResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;


import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;

import org.koulibrary.koulibraryreservationapp.mappers.LibraryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryManager libraryManager;

    private final LibraryMapper libraryMapper;

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


    public PageResponse<LibraryResponse> getAllLibraries(Pageable pageable) {

        Page<Library> libraries = libraryManager.getAllLibraries(pageable);

        List<LibraryResponse> responses = libraries.getContent().stream()
                .map(this::mapToResponse)
                .toList();


        return PageResponse.<LibraryResponse>builder()
                .content(responses)
                .pageNumber(libraries.getNumber())
                .pageSize(libraries.getSize())
                .totalElements(libraries.getTotalElements())
                .totalPages(libraries.getTotalPages())
                .isLast(libraries.isLast())
                .build();

    }

    private LibraryResponse mapToResponse(Library library) {
        return LibraryResponse.builder()
                .id(library.getId())
                .name(library.getName())
                .description(library.getDescription())
                .address(library.getAddress())
                .checkpointGraceMinutes(library.getCheckpointGraceMinutes())
                .maxActiveReservationsPerUser(library.getMaxActiveReservationsPerUser())
                .reservationWindowInDays(library.getReservationWindowInDays())
                .checkInTimeoutMinutes(library.getCheckInTimeoutMinutes())
                .checkpointIntervalMinutes(library.getCheckpointIntervalMinutes())
                .penaltyBlockDays(library.getPenaltyBlockDays())
                .build();

    }

    public LibraryResponse getLibraryById(Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        return mapToResponse(library);
    }

    public PageResponse<LibraryResponse> getLibraryByName(String name, Pageable pageable) {

        Page<Library> libraries = libraryManager.getLibraryByName(name, pageable);

        return PageResponse.<LibraryResponse>builder()
                .content(libraries.map(this::mapToResponse).getContent())
                .pageNumber(libraries.getNumber())
                .pageSize(libraries.getSize())
                .totalElements(libraries.getTotalElements())
                .totalPages(libraries.getTotalPages())
                .isLast(libraries.isLast())
                .build();
    }

    public LibraryResponse updateLibrary(Long id, @Valid UpdateLibraryRequest request) {

        Library library = libraryManager.getLibraryById(id);

        libraryMapper.updateLibraryFromDto(request,library);

        return libraryMapper.toResponse(library);
    }

    public void deleteLibrary(Long id) {

        Library library = libraryManager.getLibraryById(id);

        libraryManager.deleteLibraryById(library.getId());
    }


}
