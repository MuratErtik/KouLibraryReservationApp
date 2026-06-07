package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.LibraryStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.ClosureDoesNotBelongToThisLibrary;
import org.koulibrary.koulibraryreservationapp.exceptions.EndDateCannotBeBeforeStartDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidWorkingHourRangeException;
import org.koulibrary.koulibraryreservationapp.exceptions.WorkingHoursDoesNotBelongToThisLibrary;
import org.koulibrary.koulibraryreservationapp.managers.LibraryClosureManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryWorkingHoursManager;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryClosuresMapper;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryMapper;
import org.koulibrary.koulibraryreservationapp.mappers.LibraryWorkingHoursMapper;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryManager libraryManager;

    private final LibraryMapper libraryMapper;

    private final LibraryClosuresMapper libraryClosuresMapper;

    private final LibraryClosureManager libraryClosureManager;

    private final LibraryWorkingHoursMapper libraryWorkingHoursMapper;

    private final LibraryWorkingHoursManager libraryWorkingHoursManager;

    private final SaloonRepository saloonRepository;

    private final SlotGeneratorService slotGeneratorService;

    @Transactional
    public CreateLibraryResponse createLibrary(@Valid CreateLibraryRequest request) {

        Library library = libraryMapper.toEntity(request);

        Library savedLibrary = libraryManager.saveLibrary(library);

        return CreateLibraryResponse.builder()
                .id(savedLibrary.getId())
                .message(String.format("Library '%s' created", savedLibrary.getName()))

                .build();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public LibraryResponse getLibraryById(Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        return libraryMapper.toResponse(library);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public LibraryResponse updateLibrary(Long id, @Valid UpdateLibraryRequest request) {

        Library library = libraryManager.getLibraryById(id);
        libraryManager.checkNameConflict(library, request.getName());

        LibraryStatus oldStatus = library.getStatus();

        Library libraryToUpdate = libraryMapper.updateLibraryFromDto(request, library);
        libraryManager.updateLibrary(libraryToUpdate);

        if (libraryToUpdate.getStatus() != oldStatus) {
            recomputeForLibrary(libraryToUpdate,
                    LocalDate.now(), LocalDate.now().plusDays(SlotGeneratorService.WINDOW_DAYS));
        }

        return libraryMapper.toResponse(libraryToUpdate);
    }

    @Transactional
    public void deleteLibrary(Long id) {

        Library library = libraryManager.getLibraryById(id);

        libraryManager.deleteLibraryById(library.getId());
    }

    //Closure Methods....

    private void recomputeForLibrary(Library library, LocalDate from, LocalDate to) {
        for (Saloon saloon : saloonRepository.findByLibrary(library)) {
            slotGeneratorService.recomputeAvailability(saloon, library, from, to);
        }
    }

    private void syncForLibrary(Library library) {
        for (Saloon saloon : saloonRepository.findByLibrary(library)) {
            slotGeneratorService.syncSaloon(saloon, library);
        }
    }

    @Transactional
    public CreateLibraryClosureResponse createLibraryClosure(@Valid CreateLibraryClosureRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
        }



        LibraryClosures libraryClosures = libraryClosuresMapper.toEntity(request,library);

        LibraryClosures savedLibraryClosures = libraryClosureManager.saveLibraryClosures(libraryClosures);

        recomputeForLibrary(library, savedLibraryClosures.getStartDateTime().toLocalDate(), savedLibraryClosures.getEndDateTime().toLocalDate());

        return CreateLibraryClosureResponse.builder()
                .id(savedLibraryClosures.getId())
                .message("Library closure created successfully for " + library.getName())
                .build();

    }

    @Transactional
    public LibraryClosureResponse updateLibraryClosure(Long libraryId, Long closureId, @Valid UpdateLibraryClosureRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        if (request.getEndDateTime()!=null && request.getStartDateTime()!=null){
            if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
                throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
            }
        }

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        if (!libraryClosures.getLibrary().getId().equals(libraryId)){
            throw new ClosureDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the closure");
        }

        LocalDate oldFrom = libraryClosures.getStartDateTime().toLocalDate();
        LocalDate oldTo   = libraryClosures.getEndDateTime().toLocalDate();

        libraryClosureManager.checkDateIntervalConflict(library,libraryClosures,request.getStartDateTime(),request.getEndDateTime());

        LibraryClosures libraryClosuresToUpdate = libraryClosuresMapper.updateLibraryClosureFromDto(request,libraryClosures);

        libraryClosureManager.updateLibraryClosure(libraryClosuresToUpdate);

        LocalDate newFrom = libraryClosuresToUpdate.getStartDateTime().toLocalDate();
        LocalDate newTo   = libraryClosuresToUpdate.getEndDateTime().toLocalDate();

        LocalDate from = Stream.of(oldFrom, newFrom)
                .min(LocalDate::compareTo)
                .orElse(oldFrom);

        LocalDate to   = Stream.of(oldTo, newTo)
                .max(LocalDate::compareTo)
                .orElse(oldTo);

        recomputeForLibrary(library, from, to);

        return libraryClosuresMapper.toResponse(libraryClosures);

    }


    @Transactional(readOnly = true)
    public LibraryClosureResponse getLibraryClosureById(Long libraryId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        if (!libraryClosures.getLibrary().getId().equals(libraryId)){
            throw new ClosureDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the closure");
        }

        return libraryClosuresMapper.toResponse(libraryClosures);
    }

    @Transactional(readOnly = true)
    public PageResponse<LibraryClosureResponse> getAllLibraryClosuresByLibrary(Pageable pageable,Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        Page<LibraryClosures> libraryClosures = libraryClosureManager.getAllLibraryClosuresByLibrary(pageable,library);

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

    @Transactional
    public void deleteLibraryClosure(Long libraryId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryClosures libraryClosures = libraryClosureManager.getLibraryClosureById(closureId);

        if (!libraryClosures.getLibrary().getId().equals(libraryId)){
            throw new ClosureDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the closure");
        }

        LocalDate from = libraryClosures.getStartDateTime().toLocalDate();
        LocalDate to   = libraryClosures.getEndDateTime().toLocalDate();

        libraryClosureManager.deleteLibraryClosureId(closureId);

        recomputeForLibrary(library, from, to);

    }

    private void recomputeForLibrary(Library library) {
        recomputeForLibrary(library,
                LocalDate.now(), LocalDate.now().plusDays(SlotGeneratorService.WINDOW_DAYS));
    }

    @Transactional
    public CreateLibraryWorkingHourResponse createLibraryWorkingHour(@Valid CreateLibraryWorkingHourRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursMapper.toEntity(request,library);

        LibraryWorkingHours savedLibraryWorkingHour = libraryWorkingHoursManager.saveLibraryWorkingHours(libraryWorkingHours);



        syncForLibrary(library);


        return CreateLibraryWorkingHourResponse.builder()
                .id(savedLibraryWorkingHour.getId())
                .message("Library working hours created successfully for " + library.getName())
                .build();

    }

    @Transactional
    public LibraryWorkingHoursResponse updateLibraryWorkingHours(Long libraryId, Long workingHoursId, @Valid UpdateLibraryWorkingHoursRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);

        if (!libraryWorkingHours.getLibrary().getId().equals(libraryId)){
            throw new WorkingHoursDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the working hours");
        }

        LibraryWorkingHours libraryWorkingHoursToUpdate = libraryWorkingHoursMapper.updateLibraryWorkingHoursFromDto(request,libraryWorkingHours);

        libraryWorkingHoursManager.updateLibraryWorkingHours(libraryWorkingHoursToUpdate);

        syncForLibrary(library);

        return libraryWorkingHoursMapper.toResponse(libraryWorkingHours);

    }

    @Transactional(readOnly = true)
    public LibraryWorkingHoursResponse getLibraryWorkingHoursById(Long libraryId,Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);

        if (!libraryWorkingHours.getLibrary().getId().equals(libraryId)){
            throw new WorkingHoursDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the working hours");
        }

        return libraryWorkingHoursMapper.toResponse(libraryWorkingHours);

    }

    @Transactional(readOnly = true)
    public PageResponse<LibraryWorkingHoursResponse> getAllLibraryWorkingHoursByLibrary(Pageable pageable, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        Page<LibraryWorkingHours> libraryWorkingHours = libraryWorkingHoursManager.getAllLibraryWorkingHoursByLibrary(pageable,library);

        List<LibraryWorkingHoursResponse> responses = libraryWorkingHours.getContent().stream()
                .map(libraryWorkingHoursMapper::toResponse)
                .toList();


        return PageResponse.<LibraryWorkingHoursResponse>builder()
                .content(responses)
                .pageNumber(libraryWorkingHours.getNumber())
                .pageSize(libraryWorkingHours.getSize())
                .totalElements(libraryWorkingHours.getTotalElements())
                .totalPages(libraryWorkingHours.getTotalPages())
                .isLast(libraryWorkingHours.isLast())
                .build();

    }

    @Transactional
    public void deleteLibraryWorkingHours(Long libraryId, Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);

        if (!libraryWorkingHours.getLibrary().getId().equals(libraryId)){
            throw new WorkingHoursDoesNotBelongToThisLibrary("Library with id " + libraryId + " doesn't belong to the working hours");
        }

        libraryWorkingHoursManager.deleteLibraryWorkingHoursById(workingHoursId);

        recomputeForLibrary(library);

    }

    @Transactional(readOnly = true)
    public PageResponse<LibraryClosureResponse> getAllLibraryClosures(Pageable pageable) {


        Page<LibraryClosures> libraryClosures = libraryClosureManager.getAllLibraryClosures(pageable);

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

    @Transactional(readOnly = true)
    public PageResponse<LibraryWorkingHoursResponse> getAllLibraryWorkingHours(Pageable pageable) {



        Page<LibraryWorkingHours> libraryWorkingHours = libraryWorkingHoursManager.getAllLibraryWorkingHours(pageable);

        List<LibraryWorkingHoursResponse> responses = libraryWorkingHours.getContent().stream()
                .map(libraryWorkingHoursMapper::toResponse)
                .toList();


        return PageResponse.<LibraryWorkingHoursResponse>builder()
                .content(responses)
                .pageNumber(libraryWorkingHours.getNumber())
                .pageSize(libraryWorkingHours.getSize())
                .totalElements(libraryWorkingHours.getTotalElements())
                .totalPages(libraryWorkingHours.getTotalPages())
                .isLast(libraryWorkingHours.isLast())
                .build();

    }
}
