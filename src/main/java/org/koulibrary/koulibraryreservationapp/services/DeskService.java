package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.configs.QrImageGenerator;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.domains.QRCodeStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateDeskResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.DeskResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.QrCodeResponse;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.QrCode;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.DeskDoesNotBelongToSaloonException;
import org.koulibrary.koulibraryreservationapp.exceptions.DeskNotFoundException;
import org.koulibrary.koulibraryreservationapp.exceptions.QrCodeNotAvailableException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonDoesNotBelongToLibraryException;
import org.koulibrary.koulibraryreservationapp.managers.DeskManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.mappers.DeskMapper;
import org.koulibrary.koulibraryreservationapp.repositories.DeskRepository;
import org.koulibrary.koulibraryreservationapp.specifications.DeskSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final LibraryManager libraryManager;

    private final SaloonManager saloonManager;

    private final DeskMapper deskMapper;

    private final DeskManager deskManager;

    private final DeskRepository deskRepository;

    @Value("${app.qr.checkin-base-url}")
    private String checkinBaseUrl;


    @Transactional
    public CreateDeskResponse createDesk(@Valid CreateDeskRequest request, Long libraryId, Long saloonId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

        Desk desk = deskMapper.toEntity(request,saloon);

        QrCode qrCode = QrCode.builder()
                .code(UUID.randomUUID().toString())
                .status(QRCodeStatus.ACTIVE)
                .createdAt(LocalDateTime.now(APP_ZONE))
                .build();
        desk.setQrCode(qrCode);

        Desk savedDesk = deskManager.saveDesk(desk);

        return CreateDeskResponse.builder()
                .id(savedDesk.getId())
                .message("Desk created successfully for " + saloon.getName()+" with desk number " + desk.getDeskNumber())
                .build();


    }


    @Transactional
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PageResponse<DeskResponse> getAllDesks(Pageable pageable, Long saloonId, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

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


    @Transactional
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

    @Transactional(readOnly = true)
    public Set<DeskResponse> getAllDeskWithoutPagination(Long saloonId, Long libraryId) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

        Set<Desk> desks = deskManager.getAllDesks(saloon);

        Set<DeskResponse> responses = new HashSet<>();

        desks.forEach(desk -> responses.add(deskMapper.toResponse(desk)));

        return responses;

    }

    public  PageResponse<DeskResponse> getAllDeskByFilter(Pageable pageable, Long saloonId, Long libraryId,
                                                          DeskPolicy deskPolicy, DeskStatus deskStatus,Boolean hasPowerSocket) {


        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

        Specification<Desk> specification = (root,query,criteriaBuilder) -> criteriaBuilder.conjunction();

        if (deskPolicy != null)
            specification = specification.and(DeskSpecification.deskPolicy(deskPolicy));

        if (deskStatus != null)
            specification = specification.and(DeskSpecification.deskStatus(deskStatus));


        if (hasPowerSocket != null)
            specification = specification.and(DeskSpecification.hasDeskPowerSocket(hasPowerSocket));

        specification = specification.and(DeskSpecification.saloon(saloon));

        Page<Desk> desks = deskManager.getAllDesksWithSpec(pageable,specification);


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


    public Set<DeskResponse> getAllDeskByFilterWithoutPagination(Long saloonId, Long libraryId,
                                                                 DeskPolicy deskPolicy, DeskStatus deskStatus,Boolean hasPowerSocket) {

        Library library = libraryManager.getLibraryById(libraryId);

        Saloon saloon = saloonManager.getSaloonById(saloonId);

        if (!saloon.getLibrary().getId().equals(library.getId())) {
            throw new SaloonDoesNotBelongToLibraryException("Saloon with id "+saloonId+" doesn't belong to library with id: " + libraryId);
        }

        Specification<Desk> specification = (root,query,criteriaBuilder) -> criteriaBuilder.conjunction();

        if (deskPolicy != null)
            specification = specification.and(DeskSpecification.deskPolicy(deskPolicy));

        if (deskStatus != null)
            specification = specification.and(DeskSpecification.deskStatus(deskStatus));


        if (hasPowerSocket != null)
            specification = specification.and(DeskSpecification.hasDeskPowerSocket(hasPowerSocket));

        specification = specification.and(DeskSpecification.saloon(saloon));

        Set<Desk> desks = deskManager.getAllDesksWithSpec(specification);


        Set<DeskResponse> responses = new HashSet<>();

        desks.forEach(desk -> responses.add(deskMapper.toResponse(desk)));

        return responses;
    }


    @Transactional
    public QrCodeResponse regenerateQr(Long deskId) {
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new DeskNotFoundException("Desk not found with ID: " + deskId));

        QrCode current = desk.getQrCode();
        if (current != null) {
            current.setStatus(QRCodeStatus.REVOKED);
            current.setRevokedAt(LocalDateTime.now(APP_ZONE));
        }

        QrCode fresh = QrCode.builder()
                .code(UUID.randomUUID().toString())
                .status(QRCodeStatus.ACTIVE)
                .createdAt(LocalDateTime.now(APP_ZONE))
                .build();
        desk.setQrCode(fresh);
        deskRepository.save(desk);

        return new QrCodeResponse(desk.getId(), fresh.getCode(), fresh.getStatus());
    }

    @Transactional(readOnly = true)
    public byte[] generateQrImage(Long deskId) {
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new DeskNotFoundException("Desk not found with ID: " + deskId));
        QrCode qr = desk.getQrCode();
        if (qr == null || qr.getStatus() != QRCodeStatus.ACTIVE) {
            throw new QrCodeNotAvailableException("Desk has no active QR code: " + deskId);
        }
        String content = checkinBaseUrl + "?token=" + qr.getCode();
        return QrImageGenerator.toPng(content, 300);
    }

}
