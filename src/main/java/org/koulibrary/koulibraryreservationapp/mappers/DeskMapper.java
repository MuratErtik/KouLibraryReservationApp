package org.koulibrary.koulibraryreservationapp.mappers;


import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.DeskResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SaloonResponse;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeskMapper {

    @Mapping(target = "saloon",source = "saloon")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    @Mapping(target = "status",source = "request.status")
    Desk toEntity(CreateDeskRequest request, Saloon saloon);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Desk updateDeskFromDto(UpdateDeskRequest request,
                             @MappingTarget Desk desk);


    @Mapping(source = "library.id", target = "libraryId")
    @Mapping(source = "library.name", target = "libraryName")
    @Mapping(source = "saloon.id", target = "saloonId")
    @Mapping(source = "saloon.name", target = "saloonName")
    DeskResponse toResponse(Desk desk);





}




