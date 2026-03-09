package org.koulibrary.koulibraryreservationapp.mappers;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryClosureResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.mapstruct.*;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface LibraryClosuresMapper {

    @Mapping(source = "library.id", target = "libraryId")
    @Mapping(source = "library.name", target = "libraryName")
    @Mapping(target = "isActive", expression = "java(checkIsActive(libraryClosures))")
    LibraryClosureResponse toResponse(LibraryClosures libraryClosures);

    @Mapping(target = "library",source = "library")
    @Mapping(target = "id", ignore = true)
    LibraryClosures toEntity(CreateLibraryClosureRequest request, Library library);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LibraryClosures updateLibraryClosureFromDto(UpdateLibraryClosureRequest request,
                                     @MappingTarget LibraryClosures libraryClosures);

    default boolean checkIsActive(LibraryClosures entity) {
        if (entity == null || entity.getStartDateTime() == null || entity.getEndDateTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(entity.getStartDateTime()) && now.isBefore(entity.getEndDateTime());
    }
}
