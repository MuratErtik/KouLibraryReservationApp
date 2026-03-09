package org.koulibrary.koulibraryreservationapp.mappers;

import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateSaloonRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SaloonResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;

import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface SaloonMapper {

    @Mapping(target = "library",source = "library")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "tables", ignore = true)
    Saloon toEntity(CreateSaloonRequest request, Library library);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Saloon updateSaloonFromDto(UpdateSaloonRequest request,
                                        @MappingTarget Saloon saloon);


    @Mapping(source = "library.id", target = "libraryId")
    @Mapping(source = "library.name", target = "libraryName")
    //@Mapping(target = "currentDeskCount", expression = "java(calculateDeskCount(saloon))")
    SaloonResponse toResponse(Saloon saloon);

//    default Integer calculateDeskCount(Saloon saloon) {
//        if (saloon == null || saloon.getTables() == null) {
//            return 0;
//        }
//        return saloon.getTables().size();
//    }

}
