package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryRepository;
import org.springframework.dao.DataIntegrityViolationException;
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

//        try{
//            return libraryRepository.save(library);
//        }
//        catch (DataIntegrityViolationException e){
//            throw new LibraryAlreadyExistsException(library.getName());
//        }
    }
}
