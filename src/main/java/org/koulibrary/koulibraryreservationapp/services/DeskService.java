package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateDeskResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.DeskResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.DeskDoesNotBelongToSaloonException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonDoesNotBelongToLibraryException;
import org.koulibrary.koulibraryreservationapp.managers.DeskManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.mappers.DeskMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

        Desk desk = deskMapper.toEntity(request,saloon);

        Desk savedDesk = deskManager.saveDesk(desk);

        return CreateDeskResponse.builder()
                .id(savedDesk.getId())
                .message("Desk created successfully for " + saloon.getName()+" with desk number " + desk.getDeskNumber())
                .build();


    }


    public DeskResponse updateDesk(Long libraryId, Long saloonId, Long deskId, @Valid UpdateDeskRequest request) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }


        Desk desk = deskManager.getDeskById(deskId);

        if(!desk.getSaloon().getId().equals(saloon.getId())){
            throw new DeskDoesNotBelongToSaloonException("The Desk does not belong to the saloon with id: " + saloonId);
        }

        // number+saloon
        deskManager.checkNameConflict(desk, request.getDeskNumber());

        Desk deskToUpdate = deskMapper.updateDeskFromDto(request,desk);

        deskManager.updateDesk(deskToUpdate);

        return deskMapper.toResponse(deskToUpdate);

    }

    public DeskResponse getDeskById(Long libraryId, Long saloonId, Long deskId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }


        Desk desk = deskManager.getDeskById(deskId);

        if(!desk.getSaloon().getId().equals(saloon.getId())){
            throw new DeskDoesNotBelongToSaloonException("The Desk does not belong to the saloon with id: " + saloonId);
        }

        return deskMapper.toResponse(desk);

    }

    public PageResponse<DeskResponse> getAllDesks(Pageable pageable, Long saloonId, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        Page<Desk> desks = deskManager.getAllDesks(pageable,saloon);




        List<DeskResponse> responses = desks.getContent().stream()
                .map(deskMapper::toResponse)
                .toList();


        return PageResponse.<DeskResponse>builder()
                .content(responses)
                .pageNumber(desks.getNumber())
                .pageSize(desks.getSize())
                .totalElements(desks.getTotalElements())
                .totalPages(desks.getTotalPages())
                .isLast(desks.isLast())
                .build();
    }



    public void deleteDesk(Long libraryId, Long saloonId, Long deskId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }


        Desk desk = deskManager.getDeskById(deskId);

        if(!desk.getSaloon().getId().equals(saloon.getId())){
            throw new DeskDoesNotBelongToSaloonException("The Desk does not belong to the saloon with id: " + saloonId);
        }

        deskManager.deleteDeskById(deskId);
    }
}
