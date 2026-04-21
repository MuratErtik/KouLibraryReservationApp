package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryManager {

    private final LibraryRepository libraryRepository;

    public Library saveLibrary(Library library) {

        if (libraryRepository.existsByName(library.getName())) {
            throw new LibraryAlreadyExistsException(library.getName());
        }
        return libraryRepository.save(library);

    }

    public void updateLibrary(Library library) {
        libraryRepository.save(library);
    }

    public void checkNameConflict(Library library, String requestName) {
        if (!library.getName().equals(requestName)) {
            if (libraryRepository.existsByName(requestName)) {
                throw new LibraryAlreadyExistsException(
                        "Library with name " + requestName + " already exists"
                );
            }
        }
    }

    public Page<Library> getAllLibraries(Pageable pageable) {

        return libraryRepository.findAll(pageable);

    }

    public Library getLibraryById(Long libraryId) {

        return libraryRepository.findById(libraryId)
                .orElseThrow(() -> new LibraryNotFoundException("Library not found with id " + libraryId));

    }

    public Page<Library> getLibraryByName(String name,Pageable pageable) {

        Page<Library> libraries = libraryRepository.findByNameContainingIgnoreCase(name,pageable);

        if (libraries.isEmpty()) {
            throw new LibraryNotFoundException("Library not found with name " + name);
        }

        return libraries;
    }

    public void deleteLibraryById(Long libraryId) {

        libraryRepository.deleteById(libraryId);
    }





}
