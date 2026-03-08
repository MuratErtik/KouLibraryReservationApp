package org.koulibrary.koulibraryreservationapp.mappers;


import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryWorkingHourRequest;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryWorkingHoursMapper {

    @Mapping(target = "library",source = "library")
    @Mapping(target = "id", ignore = true)
    LibraryWorkingHours toEntity(CreateLibraryWorkingHourRequest request, Library library);
}
