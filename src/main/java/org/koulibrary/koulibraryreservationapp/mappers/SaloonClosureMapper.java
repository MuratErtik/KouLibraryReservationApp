package org.koulibrary.koulibraryreservationapp.mappers;


import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateSaloonClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SaloonClosureResponse;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonClosure;
import org.mapstruct.*;
import java.time.LocalDateTime;



@Mapper(componentModel = "spring")
public interface SaloonClosureMapper {

    @Mapping(source = "saloon.id", target = "saloonId")
    @Mapping(source = "saloon.name", target = "saloonName")
    @Mapping(target = "isActive", expression = "java(checkIsActive(saloonClosures))")
    SaloonClosureResponse toResponse(SaloonClosure saloonClosures);

    @Mapping(target = "saloon",source = "saloon")
    @Mapping(target = "id", ignore = true)
    SaloonClosure toEntity(CreateSaloonClosureRequest request, Saloon saloon);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SaloonClosure updateSaloonClosureFromDto(UpdateSaloonClosureRequest request,
                                             @MappingTarget SaloonClosure saloonClosures);

    default boolean checkIsActive(SaloonClosure entity) {
        if (entity == null || entity.getStartDateTime() == null || entity.getEndDateTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(entity.getStartDateTime()) && now.isBefore(entity.getEndDateTime());
    }
}


