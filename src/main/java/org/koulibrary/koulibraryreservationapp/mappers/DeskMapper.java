package org.koulibrary.koulibraryreservationapp.mappers;


import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;

import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeskMapper {

    @Mapping(target = "saloon",source = "saloon")
    @Mapping(target = "id", ignore = true)
    Desk toEntity(CreateDeskRequest request, Saloon saloon);


}




