package org.koulibrary.koulibraryreservationapp.mappers;

import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateLibraryRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    LibraryResponse toResponse(Library library);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLibraryFromDto(UpdateLibraryRequest request, @MappingTarget Library library);
}
