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

        libraryMapper.updateLibraryFromDto(request,library);

        return libraryMapper.toResponse(library);
    }

    public void deleteLibrary(Long id) {

        Library library = libraryManager.getLibraryById(id);

        libraryManager.deleteLibraryById(library.getId());
    }


}
