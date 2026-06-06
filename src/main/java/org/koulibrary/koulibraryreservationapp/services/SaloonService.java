package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.exceptions.*;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonClosureManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonWorkingHoursManager;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonClosureMapper;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonMapper;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonWorkingHoursMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.min;


@Service
@RequiredArgsConstructor
public class SaloonService {

    private final LibraryManager libraryManager;

    private final SaloonMapper saloonMapper;

    private final SaloonManager saloonManager;

    private final SaloonWorkingHoursMapper saloonWorkingHoursMapper;

    private final SaloonWorkingHoursManager saloonWorkingHoursManager;

    private final SaloonClosureMapper saloonClosureMapper;

    private final SaloonClosureManager saloonClosureManager;

    private final SlotGeneratorService slotGeneratorService;

    @Transactional
    public CreateSaloonResponse createSaloon(@Valid CreateSaloonRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonMapper.toEntity(request,library);

        Saloon savedSaloon = saloonManager.saveSaloon(saloon);

        return CreateSaloonResponse.builder()
                .id(savedSaloon.getId())
                .message("Saloon created successfully for " + library.getName())
                .build();
    }

    @Transactional
    public SaloonResponse updateSaloon(Long libraryId, Long saloonId, @Valid UpdateSaloonRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);
        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        String nameToCheck = request.getName() != null ? request.getName() : saloon.getName();
        Integer floorToCheck = request.getFloor() != null ? request.getFloor() : saloon.getFloor();
        if (saloonManager.findDuplicate(libraryId, floorToCheck, nameToCheck, saloonId)) {
            throw new SaloonAlreadyExistException("A saloon with the same name already exists on floor " + floorToCheck);
        }

        SaloonStatus oldStatus = saloon.getStatus();

        Saloon saloonToUpdate = saloonMapper.updateSaloonFromDto(request, saloon);
        saloonManager.updateSaloon(saloonToUpdate);

        if (saloonToUpdate.getStatus() != oldStatus) {
            slotGeneratorService.recomputeAvailability(saloonToUpdate, library);
        }

        return saloonMapper.toResponse(saloonToUpdate);
    }

    @Transactional(readOnly = true)
    public SaloonResponse getSaloonById(Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        return saloonMapper.toResponse(saloon);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaloonResponse> getAllSaloons(Pageable pageable, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);


        Page<Saloon> saloons = saloonManager.getAllSaloons(pageable,library);

        List<SaloonResponse> responses = saloons.getContent().stream()
                .map(saloonMapper::toResponse)
                .toList();


        return PageResponse.<SaloonResponse>builder()
                .content(responses)
                .pageNumber(saloons.getNumber())
                .pageSize(saloons.getSize())
                .totalElements(saloons.getTotalElements())
                .totalPages(saloons.getTotalPages())
                .isLast(saloons.isLast())
                .build();
    }

    @Transactional
    public void deleteSaloon(Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        saloonManager.deleteSaloonById(saloonId);
    }

    // --- Saloon Closure Methods ---

    @Transactional
    public CreateSaloonClosureResponse createSaloonClosure(@Valid CreateSaloonClosureRequest request, Long saloonId,Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
        }

        SaloonClosure saloonClosure = saloonClosureMapper.toEntity(request, saloon);

        SaloonClosure savedSaloonClosure = saloonClosureManager.saveSaloonClosure(saloonClosure);

        slotGeneratorService.recomputeAvailability(saloon, library,
                savedSaloonClosure.getStartDateTime().toLocalDate(), savedSaloonClosure.getEndDateTime().toLocalDate());


        return CreateSaloonClosureResponse.builder()
                .id(savedSaloonClosure.getId())
                .message("Saloon closure created successfully for Saloon: " + saloon.getName()+" Library: " + library.getName())
                .build();
    }

    @Transactional
    public SaloonClosureResponse updateSaloonClosure(Long libraryId,Long saloonId, Long closureId, @Valid UpdateSaloonClosureRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);


        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        if (request.getEndDateTime() != null && request.getStartDateTime() != null) {
            if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
                throw new EndDateCannotBeBeforeStartDateException("Start date cannot be after end date");
            }
        }

        SaloonClosure saloonClosure = saloonClosureManager.getSaloonClosureById(closureId);

        if (!saloonClosure.getSaloon().getId().equals(saloonId)) {
            throw new ClosureDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the closure");
        }

        LocalDate oldFrom = saloonClosure.getStartDateTime().toLocalDate();
        LocalDate oldTo   = saloonClosure.getEndDateTime().toLocalDate();


        saloonClosureManager.checkDateIntervalConflict(saloon, saloonClosure, request.getStartDateTime(), request.getEndDateTime());

        SaloonClosure saloonClosureToUpdate = saloonClosureMapper.updateSaloonClosureFromDto(request, saloonClosure);

        saloonClosureManager.updateSaloonClosure(saloonClosureToUpdate);

        LocalDate newFrom = saloonClosureToUpdate.getStartDateTime().toLocalDate();
        LocalDate newTo   = saloonClosureToUpdate.getEndDateTime().toLocalDate();

        LocalDate from = Stream.of(oldFrom, newFrom)
                .min(LocalDate::compareTo)
                .orElse(oldFrom);

        LocalDate to   = Stream.of(oldTo, newTo)
                .max(LocalDate::compareTo)
                .orElse(oldTo);

        slotGeneratorService.recomputeAvailability(saloon, library, from, to);

        return saloonClosureMapper.toResponse(saloonClosureToUpdate);
    }

    @Transactional(readOnly = true)
    public SaloonClosureResponse getSaloonClosureById(Long libraryId,Long saloonId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        SaloonClosure saloonClosure = saloonClosureManager.getSaloonClosureById(closureId);

        if (!saloonClosure.getSaloon().getId().equals(saloonId)) {
            throw new ClosureDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the closure");
        }

        return saloonClosureMapper.toResponse(saloonClosure);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaloonClosureResponse> getAllSaloonClosuresBySaloon(Pageable pageable,Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        Page<SaloonClosure> saloonClosures = saloonClosureManager.getAllSaloonClosureBySaloon(pageable, saloon);

        List<SaloonClosureResponse> responses = saloonClosures.getContent().stream()
                .map(saloonClosureMapper::toResponse)
                .toList();

        return PageResponse.<SaloonClosureResponse>builder()
                .content(responses)
                .pageNumber(saloonClosures.getNumber())
                .pageSize(saloonClosures.getSize())
                .totalElements(saloonClosures.getTotalElements())
                .totalPages(saloonClosures.getTotalPages())
                .isLast(saloonClosures.isLast())
                .build();
    }

    @Transactional
    public void deleteSaloonClosure(Long libraryId,Long saloonId, Long closureId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }


        SaloonClosure saloonClosure = saloonClosureManager.getSaloonClosureById(closureId);

        if (!saloonClosure.getSaloon().getId().equals(saloonId)) {
            throw new ClosureDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the closure");
        }

        LocalDate from = saloonClosure.getStartDateTime().toLocalDate();
        LocalDate to   = saloonClosure.getEndDateTime().toLocalDate();

        saloonClosureManager.deleteSaloonClosureId(closureId);

        slotGeneratorService.recomputeAvailability(saloon, library, from, to);
    }


    // --- Saloon Closure Methods ---


    // --- Saloon Working Hours Methods ---


    @Transactional
    public CreateSaloonWorkingHourResponse createLibraryWorkingHour(@Valid CreateSaloonWorkingHourRequest request, Long libraryId, Long saloonId) {


        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }


        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursMapper.toEntity(request,saloon);

        SaloonWorkingHours savedsaloonWorkingHours = saloonWorkingHoursManager.saveSaloonWorkingHours(saloonWorkingHours);

        return CreateSaloonWorkingHourResponse.builder()
                .id(savedsaloonWorkingHours.getId())
                .message("Saloon working hours created successfully for " + saloon.getName()+" and library " + library.getName())
                .build();


    }

    @Transactional
    public SaloonWorkingHoursResponse updateSaloonWorkingHours(Long libraryId, Long saloonId, Long workingHoursId,
                                                               @Valid UpdateSaloonWorkingHoursRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);


        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }


        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursManager.getSaloonWorkingHoursById(workingHoursId);

        if (!saloonWorkingHours.getSaloon().getId().equals(saloonId)) {
            throw new WorkingHoursDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the workinghours with id " + workingHoursId);
        }

        SaloonWorkingHours saloonWorkingHoursToUpdate = saloonWorkingHoursMapper.updateSaloonWorkingHoursFromDto(request,saloonWorkingHours);

        saloonWorkingHoursManager.updateSaloonWorkingHours(saloonWorkingHoursToUpdate);

        return saloonWorkingHoursMapper.toResponse(saloonWorkingHours);

    }

    @Transactional(readOnly = true)
    public SaloonWorkingHoursResponse getSaloonWorkingHoursById(Long libraryId, Long saloonId, Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);


        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursManager.getSaloonWorkingHoursById(workingHoursId);

        if (!saloonWorkingHours.getSaloon().getId().equals(saloonId)) {
            throw new WorkingHoursDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the workinghours with id " + workingHoursId);
        }

        return saloonWorkingHoursMapper.toResponse(saloonWorkingHours);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaloonWorkingHoursResponse> getAllWorkingHoursBySaloon(Pageable pageable, Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }


        Page<SaloonWorkingHours> saloonWorkingHours = saloonWorkingHoursManager.getAllSaloonWorkingHours(pageable,saloon);

        List<SaloonWorkingHoursResponse> responses = saloonWorkingHours.getContent().stream()
                .map(saloonWorkingHoursMapper::toResponse)
                .toList();

        return PageResponse.<SaloonWorkingHoursResponse>builder()
                .content(responses)
                .pageNumber(saloonWorkingHours.getNumber())
                .pageSize(saloonWorkingHours.getSize())
                .totalElements(saloonWorkingHours.getTotalElements())
                .totalPages(saloonWorkingHours.getTotalPages())
                .isLast(saloonWorkingHours.isLast())
                .build();

    }

    @Transactional
    public void deleteSaloonWorkingHours(Long libraryId, Long saloonId, Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(libraryId)) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon does not belong to the library with id " + libraryId);
        }

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursManager.getSaloonWorkingHoursById(workingHoursId);

        if (!saloonWorkingHours.getSaloon().getId().equals(saloonId)) {
            throw new WorkingHoursDoesNotBelongToThisSaloon("Saloon with id " + saloonId + " doesn't belong to the workinghours with id " + workingHoursId);
        }

        saloonWorkingHoursManager.deleteSaloonWorkingHoursById(workingHoursId);
    }

}