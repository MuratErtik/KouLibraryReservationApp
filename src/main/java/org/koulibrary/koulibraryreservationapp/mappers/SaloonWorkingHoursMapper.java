package org.koulibrary.koulibraryreservationapp.mappers;

import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryWorkingHourRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateSaloonWorkingHourRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryWorkingHoursRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateSaloonWorkingHoursRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryWorkingHoursResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SaloonWorkingHoursResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonWorkingHours;
import org.mapstruct.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface SaloonWorkingHoursMapper {

    @Mapping(target = "saloon",source = "saloon")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isClosed", constant = "false")
    SaloonWorkingHours toEntity(CreateSaloonWorkingHourRequest request, Saloon saloon);


    @Mapping(source = "saloon.library.id", target = "libraryId")
    @Mapping(source = "saloon.library.name", target = "libraryName")
    @Mapping(source = "saloon.id", target = "saloonId")
    @Mapping(source = "saloon.name", target = "saloonName")
    @Mapping(target = "isCurrentlyOpen", expression = "java(calculateIsCurrentlyOpen(saloonWorkingHours))")
    SaloonWorkingHoursResponse toResponse(SaloonWorkingHours saloonWorkingHours);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SaloonWorkingHours updateSaloonWorkingHoursFromDto(UpdateSaloonWorkingHoursRequest request,
                                                       @MappingTarget SaloonWorkingHours saloonWorkingHours);




    default Boolean calculateIsCurrentlyOpen(SaloonWorkingHours entity) {
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
