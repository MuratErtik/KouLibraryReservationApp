package org.koulibrary.koulibraryreservationapp.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateDeskRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.CreateDeskResponse;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonDoesNotBelongToLibraryException;
import org.koulibrary.koulibraryreservationapp.managers.DeskManager;
import org.koulibrary.koulibraryreservationapp.managers.LibraryManager;
import org.koulibrary.koulibraryreservationapp.managers.SaloonManager;
import org.koulibrary.koulibraryreservationapp.mappers.DeskMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DeskServiceTest {

    @InjectMocks
    private DeskService deskService;

    @Mock
    private LibraryManager libraryManager;

    @Mock
    private SaloonManager saloonManager;

    @Mock
    private DeskMapper deskMapper;

    @Mock
    private DeskManager deskManager;


    @Test
    void createDesk() throws Exception {

        Long libraryId = 1L;
        Long saloonId = 2L;

        CreateDeskRequest createDeskRequest = new CreateDeskRequest();
        createDeskRequest.setDeskNumber(1);
        createDeskRequest.setPolicy(DeskPolicy.HYBRID);
        createDeskRequest.setStatus(DeskStatus.OCCUPIED);
        createDeskRequest.setHasPowerSocket(false);

        Library libraryMock = mock(Library.class);
        when(libraryMock.getId()).thenReturn(libraryId);

        Saloon saloonMock = mock(Saloon.class);
        //when(saloonMock.getId()).thenReturn(saloonId);
        when(saloonMock.getName()).thenReturn("Test Saloon");
        when(saloonMock.getLibrary()).thenReturn(libraryMock);


        Desk deskMock = mock(Desk.class);
        when(deskMock.getId()).thenReturn(1L);
        when(deskMock.getDeskNumber()).thenReturn(1);


        when(libraryManager.getLibraryById(libraryId)).thenReturn(libraryMock);
        when(saloonManager.getSaloonById(saloonId)).thenReturn(saloonMock);
        when(deskMapper.toEntity(any(CreateDeskRequest.class), any(Saloon.class))).thenReturn(deskMock);
        when(deskManager.saveDesk(any(Desk.class))).thenReturn(deskMock);

        CreateDeskResponse response = deskService.createDesk(createDeskRequest, libraryId, saloonId);

        assertEquals(deskMock.getId(), response.getId());
        assertNotNull(response.getMessage());


    }

    @Test
    void createDesk_shouldThrow_whenSaloonNotBelongsToLibrary() {

        Library library1 = mock(Library.class);
        when(library1.getId()).thenReturn(1L);

        Library library2 = mock(Library.class);
        when(library2.getId()).thenReturn(99L); // different library!

        Saloon saloonMock = mock(Saloon.class);
        when(saloonMock.getLibrary()).thenReturn(library2);

        when(libraryManager.getLibraryById(1L)).thenReturn(library1);
        when(saloonManager.getSaloonById(2L)).thenReturn(saloonMock);

        assertThrows(SaloonDoesNotBelongToLibraryException.class,
                () -> deskService.createDesk(new CreateDeskRequest(), 1L, 2L));
    }



}