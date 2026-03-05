package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateLibraryResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // list just one library by some filter
    //name,id
    @GetMapping
    public ResponseEntity<PageResponse<LibraryResponse>> getLibraryByName(@RequestParam String name,
                                                                  @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getLibraryByName(name,pageable));
    }

    @GetMapping("/{libraryId}")
    public ResponseEntity<LibraryResponse> getLibraryById(@PathVariable Long libraryId) {

        return ResponseEntity.ok(libraryService.getLibraryById(libraryId));
    }

    
    @GetMapping("get-all")
    public ResponseEntity<PageResponse<LibraryResponse>> getAllLibraries(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getAllLibraries(pageable));
    }

    //Change Rules
}
