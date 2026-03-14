package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.*;
import org.koulibrary.koulibraryreservationapp.services.SaloonService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.SALOONCONTROLLER;

@RestController
@RequestMapping(SALOONCONTROLLER)
@RequiredArgsConstructor
public class SaloonController {

    private final SaloonService saloonService;



    @PostMapping
    public ResponseEntity<CreateSaloonResponse> createSaloon(@Valid @RequestBody CreateSaloonRequest request, @PathVariable Long libraryId) {

        CreateSaloonResponse response = saloonService.createSaloon(request,libraryId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/update-saloon/{saloonId}")
    public ResponseEntity<SaloonResponse> updateSaloon(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @Valid @RequestBody UpdateSaloonRequest request) {

        SaloonResponse response = saloonService.updateSaloon(libraryId,saloonId,request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/get-saloon/{saloonId}")
    public ResponseEntity<SaloonResponse> getSaloonById(@PathVariable Long libraryId, @PathVariable Long saloonId) {

        return ResponseEntity.ok(saloonService.getSaloonById(libraryId,saloonId));
    }

    @GetMapping("/get-all-saloons")
    public ResponseEntity<PageResponse<SaloonResponse>> getALlSaloons(
            @PathVariable Long libraryId,
            @PageableDefault(size = 10, sort = "library") Pageable pageable) {

        return ResponseEntity.ok(saloonService.getAllSaloons(pageable,libraryId));
    }


    @DeleteMapping("/delete-saloon/{saloonId}")
    public ResponseEntity<Void> deleteSaloon(@PathVariable Long libraryId, @PathVariable Long saloonId) {


        saloonService.deleteSaloon(libraryId,saloonId);
        return ResponseEntity.noContent().build();


    }

    // saloon working hours endpoints....


    @PostMapping("/{saloonId}/create-working-hours")
    public ResponseEntity<CreateSaloonWorkingHourResponse> createSaloonWorkingHour(@Valid @RequestBody CreateSaloonWorkingHourRequest request,
                                                                                   @PathVariable Long libraryId,
                                                                                   @PathVariable Long saloonId) {

        CreateSaloonWorkingHourResponse response = saloonService.createLibraryWorkingHour(request,libraryId,saloonId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping("/{saloonId}/update-working-hours/{workingHoursId}")
    public ResponseEntity<SaloonWorkingHoursResponse> updateSaloonWorkingHours(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @PathVariable Long workingHoursId,
            @Valid @RequestBody UpdateSaloonWorkingHoursRequest request) {

        SaloonWorkingHoursResponse response = saloonService.updateSaloonWorkingHours(libraryId,saloonId,workingHoursId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/{saloonId}/get-working-hours/{workingHoursId}")
    public ResponseEntity<SaloonWorkingHoursResponse> getSaloonWorkingHoursById(@PathVariable Long libraryId, @PathVariable Long workingHoursId,
                                                                                @PathVariable Long saloonId) {

        return ResponseEntity.ok(saloonService.getSaloonWorkingHoursById(libraryId,saloonId,workingHoursId));
    }



    @GetMapping("/{saloonId}/get-all-working-hours")
    public ResponseEntity<PageResponse<SaloonWorkingHoursResponse>> getAllSaloonWorkingHours(
            @PathVariable Long libraryId,
            @PathVariable Long saloonId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(saloonService.getAllLibrarySaloonHours(pageable,libraryId,saloonId));
    }

//
//
//    @DeleteMapping("/{libraryId}/delete-working-hours/{workingHoursId}")
//    public ResponseEntity<Void> deleteLibraryWorkingHours(@PathVariable Long libraryId, @PathVariable Long workingHoursId) {
//
//
//        libraryService.deleteLibraryWorkingHours(libraryId,workingHoursId);
//        return ResponseEntity.noContent().build();
//
//
//    }





}
