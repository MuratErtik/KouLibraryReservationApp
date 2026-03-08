package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.entities.Library;


import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.exceptions.EndDateCannotBeBeforeStartDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidWorkingHourRangeException;
import org.koulibrary.koulibraryreservationapp.managers.LibraryClosureManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;

import org.koulibrary.koulibraryreservationapp.managers.LibraryWorkingHoursManager;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryClosuresMapper;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryMapper;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryWorkingHoursMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryManager libraryManager;

    private final LibraryMapper libraryMapper;

    private final LibraryClosuresMapper libraryClosuresMapper;

    private final LibraryClosureManager libraryClosureManager;

    private final LibraryWorkingHoursMapper libraryWorkingHoursMapper;

    private final LibraryWorkingHoursManager libraryWorkingHoursManager;

    public CreateLibraryResponse createLibrary(@Valid CreateLibraryRequest request) {

        Library library = libraryMapper.toEntity(request);

        Library savedLibrary = libraryManager.saveLibrary(library);

        return CreateLibraryResponse.builder()
                .id(savedLibrary.getId())
                .message(String.format("Library '%s' created", savedLibrary.getName()))

                .build();
    }

    public PageResponse<LibraryResponse> getAllLibraries(Pageable pageable) {

        Page<Library> libraries = libraryManager.getAllLibraries(pageable);

        List<LibraryResponse> responses = libraries.getContent().stream()
                .map(libraryMapper::toResponse)
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

    public LibraryResponse getLibraryById(Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        return libraryMapper.toResponse(library);
    }

    public PageResponse<LibraryResponse> getLibraryByName(String name, Pageable pageable) {

        Page<Library> libraries = libraryManager.getLibraryByName(name, pageable);

        return PageResponse.<LibraryResponse>builder()
                .content(libraries.map((libraryMapper::toResponse)).getContent())
                .pageNumber(libraries.getNumber())
                .pageSize(libraries.getSize())
                .totalElements(libraries.getTotalElements())
                .totalPages(libraries.getTotalPages())
                .isLast(libraries.isLast())
                .build();
    }

    public LibraryResponse updateLibrary(Long id, @Valid UpdateLibraryRequest request) {

        Library library = libraryManager.getLibraryById(id);


        Library libraryToUpdate = libraryMapper.updateLibraryFromDto(request,library);

        libraryManager.updateLibrary(libraryToUpdate);

        return libraryMapper.toResponse(library);
    }

    public void deleteLibrary(Long id) {

        Library library = libraryManager.getLibraryById(id);

        libraryManager.deleteLibraryById(library.getId());
    }

    //Closure Methods....

    public CreateLibraryClosureResponse createLibraryClosure(@Valid CreateLibraryClosureRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
        }

        LibraryClosures libraryClosures = libraryClosuresMapper.toEntity(request,library);

        LibraryClosures savedLibraryClosures = libraryClosureManager.saveLibraryClosures(libraryClosures);

        return CreateLibraryClosureResponse.builder()
                .id(savedLibraryClosures.getId())
                .message("Library closure created successfully for " + library.getName())
                .build();

    }

    public LibraryClosureResponse updateLibraryClosure(Long libraryId, Long closureId, @Valid UpdateLibraryClosureRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        if (request.getEndDateTime()!=null && request.getStartDateTime()!=null){
            if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
                throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
            }
        }

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        LibraryClosures libraryClosuresToUpdate = libraryClosuresMapper.updateLibraryClosureFromDto(request,libraryClosures);

        libraryClosureManager.updateLibraryClosure(libraryClosuresToUpdate);

        return libraryClosuresMapper.toResponse(libraryClosures);

    }


    public LibraryClosureResponse getLibraryClosureById(Long libraryId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        return libraryClosuresMapper.toResponse(libraryClosures);
    }

    public PageResponse<LibraryClosureResponse> getAllLibraryClosures(Pageable pageable,Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        Page<LibraryClosures> libraryClosures = libraryClosureManager.getAllLibraryClosures(pageable,library);

        List<LibraryClosureResponse> responses = libraryClosures.getContent().stream()
                .map(libraryClosuresMapper::toResponse)
                .toList();


        return PageResponse.<LibraryClosureResponse>builder()
                .content(responses)
                .pageNumber(libraryClosures.getNumber())
                .pageSize(libraryClosures.getSize())
                .totalElements(libraryClosures.getTotalElements())
                .totalPages(libraryClosures.getTotalPages())
                .isLast(libraryClosures.isLast())
                .build();



    }

    public void deleteLibraryClosure(Long libraryId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        libraryClosureManager.deleteLibraryClosureId(closureId);


    }

    public CreateLibraryWorkingHourResponse createLibraryWorkingHour(@Valid CreateLibraryWorkingHourRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursMapper.toEntity(request,library);

        LibraryWorkingHours savedLibraryWorkingHour = libraryWorkingHoursManager.saveLibraryWorkingHours(libraryWorkingHours);

        return CreateLibraryWorkingHourResponse.builder()
                .id(savedLibraryWorkingHour.getId())
                .message("Library working hours created successfully for " + library.getName())
                .build();

    }

    public LibraryWorkingHoursResponse updateLibraryWorkingHours(Long libraryId, Long workingHoursId, @Valid UpdateLibraryWorkingHoursRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);

        LibraryWorkingHours libraryWorkingHoursToUpdate = libraryWorkingHoursMapper.updateLibraryWorkingHoursFromDto(request,libraryWorkingHours);

        libraryWorkingHoursManager.updateLibraryWorkingHours(libraryWorkingHoursToUpdate);

        return libraryWorkingHoursMapper.toResponse(libraryWorkingHours);

    }

    public LibraryWorkingHoursResponse getLibraryWorkingHoursById(Long libraryId, Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);

        return libraryWorkingHoursMapper.toResponse(libraryWorkingHours);

    }
}
