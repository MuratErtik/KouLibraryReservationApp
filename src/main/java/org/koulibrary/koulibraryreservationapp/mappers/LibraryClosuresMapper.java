package org.koulibrary.koulibraryreservationapp.mappers;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateLibraryClosureRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.LibraryClosureResponse;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LibraryClosuresMapper {

    LibraryClosureResponse toResponse(LibraryClosures libraryClosures);

    @Mapping(target = "library",source = "library")
    LibraryClosures toEntity(CreateLibraryClosureRequest request, Library library);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateLibraryFromDto(UpdateLibraryRequest request, @MappingTarget Library library);
}
