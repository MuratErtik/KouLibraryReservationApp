package org.koulibrary.koulibraryreservationapp.mappers;

import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.entities.Library;

import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface SaloonMapper {

    @Mapping(target = "library",source = "library")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "tables", ignore = true)
    Saloon toEntity(CreateSaloonRequest request, Library library);


}
