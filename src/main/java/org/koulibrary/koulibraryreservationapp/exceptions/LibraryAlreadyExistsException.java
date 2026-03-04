package org.koulibrary.koulibraryreservationapp.exceptions;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LibraryAlreadyExistsException extends RuntimeException {
    public LibraryAlreadyExistsException(String libraryName) {

      super("Library with name " + libraryName + " already exists");

    }

}
