package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.exceptions.EndDateCannotBeBeforeStartDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidWorkingHourRangeException;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryWorkingHoursManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonWorkingHoursManager;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonMapper;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonWorkingHoursMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaloonService {

    private final LibraryManager libraryManager;

    private final SaloonMapper saloonMapper;

    private final SaloonManager saloonManager;

    private final SaloonWorkingHoursMapper saloonWorkingHoursMapper;

    private final SaloonWorkingHoursManager saloonWorkingHoursManager;


    public CreateSaloonResponse createSaloon(@Valid CreateSaloonRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonMapper.toEntity(request,library);

        Saloon savedSaloon = saloonManager.saveSaloon(saloon);

        return CreateSaloonResponse.builder()
                .id(savedSaloon.getId())
                .message("Saloon created successfully for " + library.getName())
                .build();
    }

    public SaloonResponse updateSaloon(Long libraryId, Long saloonId, @Valid UpdateSaloonRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        Saloon saloonToUpdate = saloonMapper.updateSaloonFromDto(request,saloon);

        saloonManager.updateSaloon(saloonToUpdate);

        return saloonMapper.toResponse(saloon);


    }

    public SaloonResponse getSaloonById(Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        return saloonMapper.toResponse(saloon);
    }

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

    public void deleteSaloon(Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        saloonManager.deleteSaloonById(saloonId);
    }

    public CreateSaloonWorkingHourResponse createLibraryWorkingHour(@Valid CreateSaloonWorkingHourRequest request, Long libraryId, Long saloonId) {


        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);


        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursMapper.toEntity(request,saloon);

        SaloonWorkingHours savedsaloonWorkingHours = saloonWorkingHoursManager.saveSaloonWorkingHours(saloonWorkingHours);

        return CreateSaloonWorkingHourResponse.builder()
                .id(savedsaloonWorkingHours.getId())
                .message("Saloon working hours created successfully for " + saloon.getName())
                .build();


    }

    public SaloonWorkingHoursResponse updateSaloonWorkingHours(Long libraryId, Long saloonId, Long workingHoursId,
                                                               @Valid UpdateSaloonWorkingHoursRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new InvalidWorkingHourRangeException("Closing time must be after opening time");
        }

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursManager.getSaloonWorkingHoursById(workingHoursId);

        SaloonWorkingHours saloonWorkingHoursToUpdate = saloonWorkingHoursMapper.updateSaloonWorkingHoursFromDto(request,saloonWorkingHours);

        saloonWorkingHoursManager.updateSaloonWorkingHours(saloonWorkingHoursToUpdate);

        return saloonWorkingHoursMapper.toResponse(saloonWorkingHours);

    }

    public SaloonWorkingHoursResponse getSaloonWorkingHoursById(Long libraryId, Long saloonId, Long workingHoursId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        SaloonWorkingHours saloonWorkingHours = saloonWorkingHoursManager.getSaloonWorkingHoursById(workingHoursId);

        return saloonWorkingHoursMapper.toResponse(saloonWorkingHours);
    }




//
//    public PageResponse<LibraryWorkingHoursResponse> getAllLibraryWorkingHours(Pageable pageable, Long libraryId) {
//
//        Library library = libraryManager.getLibraryById(libraryId);
//
//
//        Page<LibraryWorkingHours> libraryWorkingHours = libraryWorkingHoursManager.getAllLibraryWorkingClosure(pageable,library);
//
//        List<LibraryWorkingHoursResponse> responses = libraryWorkingHours.getContent().stream()
//                .map(libraryWorkingHoursMapper::toResponse)
//                .toList();
//
//
//        return PageResponse.<LibraryWorkingHoursResponse>builder()
//                .content(responses)
//                .pageNumber(libraryWorkingHours.getNumber())
//                .pageSize(libraryWorkingHours.getSize())
//                .totalElements(libraryWorkingHours.getTotalElements())
//                .totalPages(libraryWorkingHours.getTotalPages())
//                .isLast(libraryWorkingHours.isLast())
//                .build();
//
//    }
//
//    public void deleteLibraryWorkingHours(Long libraryId, Long workingHoursId) {
//
//        Library library = libraryManager.getLibraryById(libraryId);
//
//        LibraryWorkingHours libraryWorkingHours = libraryWorkingHoursManager.getLibraryWorkingHoursById(workingHoursId);
//
//        libraryWorkingHoursManager.deleteLibraryWorkingHoursById(workingHoursId);
//
//    }
}