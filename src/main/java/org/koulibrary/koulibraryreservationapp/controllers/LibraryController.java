package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PatchMapping("/{id}")
    public ResponseEntity<LibraryResponse> updateLibrary(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLibraryRequest request) {

        LibraryResponse response= libraryService.updateLibrary(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    //Listing

    // list just one library by some filter
    @GetMapping("/search")
    public ResponseEntity<PageResponse<LibraryResponse>> getLibraryByName(@RequestParam String name,
                                                                  @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getLibraryByName(name,pageable));
    }

    @GetMapping("/{libraryId}")
    public ResponseEntity<LibraryResponse> getLibraryById(@PathVariable Long libraryId) {

        return ResponseEntity.ok(libraryService.getLibraryById(libraryId));
    }

    
    @GetMapping
    public ResponseEntity<PageResponse<LibraryResponse>> getAllLibraries(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getAllLibraries(pageable));
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long libraryId) {


        libraryService.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();


    }

    //library closure endpoints

    //CRUD
    @PostMapping(LIBRARYCLOSURECONTROLLER)
    public ResponseEntity<CreateLibraryClosureResponse> createLibraryClosure(@Valid @RequestBody CreateLibraryClosureRequest request, @PathVariable Long libraryId) {

        CreateLibraryClosureResponse response = libraryService.createLibraryClosure(request,libraryId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping(LIBRARYCLOSURECONTROLLER+"/{closureId}")
    public ResponseEntity<LibraryClosureResponse> updateLibraryClosure(
            @PathVariable Long libraryId,
            @PathVariable Long closureId,
            @Valid @RequestBody UpdateLibraryClosureRequest request) {

        LibraryClosureResponse response = libraryService.updateLibraryClosure(libraryId,closureId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(LIBRARYCLOSURECONTROLLER+"/{closureId}")
    public ResponseEntity<LibraryClosureResponse> getLibraryClosureById(@PathVariable Long libraryId, @PathVariable Long closureId) {

        return ResponseEntity.ok(libraryService.getLibraryClosureById(libraryId,closureId));
    }

    @GetMapping(LIBRARYCLOSURECONTROLLER+"/by-library")
    public ResponseEntity<PageResponse<LibraryClosureResponse>> getAllLibraryClosureByLibrary(
            @PathVariable Long libraryId,
            @PageableDefault(size = 10, sort = "startDateTime") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getAllLibraryClosuresByLibrary(pageable,libraryId));
    }

    @GetMapping(LIBRARYCLOSURECONTROLLER)
    public ResponseEntity<PageResponse<LibraryClosureResponse>> getAllLibraryClosure(@PageableDefault(size = 10, sort = "startDateTime") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getAllLibraryClosures(pageable));
    }



    @DeleteMapping(LIBRARYCLOSURECONTROLLER+"/{closureId}")
    public ResponseEntity<Void> deleteLibraryClosure(@PathVariable Long libraryId, @PathVariable Long closureId) {


        libraryService.deleteLibraryClosure(libraryId,closureId);
        return ResponseEntity.noContent().build();


    }


    //library WorkingHours endpoints
    @PostMapping(LIBRARYWORKINGHOURSCONTROLLER)
    public ResponseEntity<CreateLibraryWorkingHourResponse> createLibraryWorkingHour(@Valid @RequestBody CreateLibraryWorkingHourRequest request,
                                                                                     @PathVariable Long libraryId) {

        CreateLibraryWorkingHourResponse response = libraryService.createLibraryWorkingHour(request,libraryId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping(LIBRARYWORKINGHOURSCONTROLLER+"/{workingHoursId}")
    public ResponseEntity<LibraryWorkingHoursResponse> updateLibraryWorkingHours(
            @PathVariable Long libraryId,
            @PathVariable Long workingHoursId,
            @Valid @RequestBody UpdateLibraryWorkingHoursRequest request) {

        LibraryWorkingHoursResponse response = libraryService.updateLibraryWorkingHours(libraryId,workingHoursId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/get-working-hours-by-library/{workingHoursId}")
    public ResponseEntity<LibraryWorkingHoursResponse> getLibraryWorkingHoursById(@PathVariable Long workingHoursId) {

        return ResponseEntity.ok(libraryService.getLibraryWorkingHoursById(workingHoursId));
    }



    @GetMapping(LIBRARYWORKINGHOURSCONTROLLER+"/by-library")
    public ResponseEntity<PageResponse<LibraryWorkingHoursResponse>> getAllLibraryWorkingHoursByLibrary(
            @PathVariable Long libraryId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(libraryService.getAllLibraryWorkingHoursByLibrary(pageable,libraryId));
    }

    @GetMapping(LIBRARYWORKINGHOURSCONTROLLER)
    public ResponseEntity<PageResponse<LibraryWorkingHoursResponse>> getAllLibraryWorkingHours(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(libraryService.getAllLibraryWorkingHours(pageable));
    }


    @DeleteMapping(LIBRARYWORKINGHOURSCONTROLLER+"/{workingHoursId}")
    public ResponseEntity<Void> deleteLibraryWorkingHours(@PathVariable Long libraryId, @PathVariable Long workingHoursId) {


        libraryService.deleteLibraryWorkingHours(libraryId,workingHoursId);
        return ResponseEntity.noContent().build();


    }

}
