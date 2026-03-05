package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LibraryManager {

    private final LibraryRepository libraryRepository;

    @Transactional
    public Library saveLibrary(Library library) {

        if (libraryRepository.existsByName(library.getName())) {
            throw new LibraryAlreadyExistsException(library.getName());
        }
        return libraryRepository.save(library);

    }

    @Transactional(readOnly = true)
    public Page<Library> getAllLibraries(Pageable pageable) {

        return libraryRepository.findAll(pageable);

    }

    @Transactional(readOnly = true)
    public Library getLibraryById(Long libraryId) {

        return libraryRepository.findById(libraryId)
                .orElseThrow(() -> new LibraryNotFoundException("Library not found with id " + libraryId));

    }

    @Transactional(readOnly = true)
    public Page<Library> getLibraryByName(String name,Pageable pageable) {

        Page<Library> libraries = libraryRepository.findByNameContainingIgnoreCase(name,pageable);

        if (libraries.isEmpty()) {
            throw new LibraryNotFoundException("Library not found with name " + name);
        }

        return libraries;
    }

    @Transactional
    public void deleteLibraryById(Long libraryId) {

        libraryRepository.deleteById(libraryId);
    }





}
