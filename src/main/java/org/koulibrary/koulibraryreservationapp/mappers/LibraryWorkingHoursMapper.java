package org.koulibrary.koulibraryreservationapp.mappers;


import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryWorkingHourRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryWorkingHoursRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryWorkingHoursResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.mapstruct.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface LibraryWorkingHoursMapper {

    @Mapping(target = "library",source = "library")
    @Mapping(target = "id", ignore = true)
    LibraryWorkingHours toEntity(CreateLibraryWorkingHourRequest request, Library library);


    @Mapping(source = "library.id", target = "libraryId")
    @Mapping(source = "library.name", target = "libraryName")
    @Mapping(target = "isCurrentlyOpen", expression = "java(calculateIsCurrentlyOpen(libraryWorkingHours))")
    LibraryWorkingHoursResponse toResponse(LibraryWorkingHours libraryWorkingHours);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LibraryWorkingHours updateLibraryWorkingHoursFromDto(UpdateLibraryWorkingHoursRequest request,
                                                         @MappingTarget LibraryWorkingHours libraryWorkingHours);




    default Boolean calculateIsCurrentlyOpen(LibraryWorkingHours entity) {
        if (entity == null) {
            return false;
        }

        DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();

        if (!entity.getDayOfWeek().equals(today)) {
            return false;
        }

        LocalTime now = LocalTime.now();
        return !now.isBefore(entity.getOpeningTime()) && now.isBefore(entity.getClosingTime());
    }

}
