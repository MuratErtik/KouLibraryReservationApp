package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateLibraryClosureResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateSaloonResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.EndDateCannotBeBeforeStartDateException;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.mappers.SaloonMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaloonService {

    private final LibraryManager libraryManager;

    private final SaloonMapper saloonMapper;

    private final SaloonManager saloonManager;


    public CreateSaloonResponse createSaloon(@Valid CreateSaloonRequest request, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonMapper.toEntity(request,library);

        Saloon savedSaloon = saloonManager.saveSaloon(saloon);

        return CreateSaloonResponse.builder()
                .id(savedSaloon.getId())
                .message("Saloon created successfully for " + library.getName())
                .build();
    }
}
