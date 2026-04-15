package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateDeskResponse;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.managers.DeskManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.mappers.DeskMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final LibraryManager libraryManager;

    private final SaloonManager saloonManager;

    private final DeskMapper deskMapper;

    private final DeskManager deskManager;


    public CreateDeskResponse createDesk(@Valid CreateDeskRequest request, Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        Desk desk = deskMapper.toEntity(request,saloon);

        Desk savedDesk = deskManager.saveDesk(desk);

        return CreateDeskResponse.builder()
                .id(savedDesk.getId())
                .message("Desk created successfully for " + saloon.getName()+" with desk number " + desk.getDeskNumber())
                .build();


    }
}
