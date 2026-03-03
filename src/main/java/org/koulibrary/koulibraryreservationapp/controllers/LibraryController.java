package org.koulibrary.koulibraryreservationapp.controllers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.services.LibraryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.*;

@RestController
@RequestMapping(LIBRARYCONTROLLER)
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    //Create


    //Update

    //Listing

    //Change Rules
}
