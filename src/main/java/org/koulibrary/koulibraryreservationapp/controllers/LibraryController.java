package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateLibraryResponse;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.*;

@RestController
@RequestMapping(LIBRARYCONTROLLER)
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    //Create
    // security has omitted for a while @RequestHeader("Authorization") String jwt
    //ab -n 2 -c 2 -s 600 -p createLibrary.json -T "application/json" ([...](http://localhost:8080/dev/v1/libraries))
    // it tested and concurrency done.
    @PostMapping
    public ResponseEntity<CreateLibraryResponse> createLibrary(@Valid @RequestBody CreateLibraryRequest request) {

        CreateLibraryResponse response = libraryService.createLibrary(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    //Update

    //Listing

    //Change Rules
}
