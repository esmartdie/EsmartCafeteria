package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.exception.*;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.*;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IReservationRecordRepository reservationRecordRepository;

    @Mock
    private IReservationRepository reservationRepository;

    @Spy
    private DTOConverter converter;

    @InjectMocks
    private ReservationService reservationService;

    private NewReservationDTO reservationDTO;
    private Client client;
    private ReservationRecord reservationRecord;
    private Reservation savedReservation;
    private ReservationDTO reservationDTOResult;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role(1L, "ROLE_USER");


        client = new Client(1L, "John", "Doe", "john.doe@example.com", "password123", true, role);
        client.setRating(5.0);


        reservationRecord = new ReservationRecord();
        reservationRecord.setId(1L);
        reservationRecord.setEmptySpaces(10);
        reservationRecord.setReservationDate(LocalDate.now().plusDays(1));
        reservationRecord.setShift(Shift.DAY1);


        clientDTO = new ClientDTO(1L, "John", "Doe", "john.doe@example.com", true);
        reservationDTO = new NewReservationDTO();
        reservationDTO.setClientDTO(clientDTO);
        reservationDTO.setDinners(4);
        reservationDTO.setReservationDate(LocalDate.now().plusDays(1));
        reservationDTO.setShift(Shift.DAY1);


        savedReservation = new Reservation(client, 4, reservationRecord, LocalDate.now().plusDays(1), Shift.DAY1);
        savedReservation.setId(1L);
        savedReservation.setReservationStatus(ReservationStatus.ACCEPTED);


        reservationDTOResult = new ReservationDTO();
        reservationDTOResult.setId(1L);
        reservationDTOResult.setDinners(4);
        reservationDTOResult.setReservationDate(LocalDate.now().plusDays(1));
        reservationDTOResult.setShift(Shift.DAY1);
        reservationDTOResult.setReservationStatus(ReservationStatus.ACCEPTED);
        reservationDTOResult.setClientDTO(clientDTO);
    }

    @Test
    void createReservation_HappyPath() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(reservationRecord));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);


        ReservationDTO result = reservationService.createReservation(reservationDTO);

        assertNotNull(result);
        assertEquals(reservationDTOResult.getId(), result.getId());
        assertEquals(reservationDTOResult.getDinners(), result.getDinners());
        assertEquals(reservationDTOResult.getReservationDate(), result.getReservationDate());
        assertEquals(reservationDTOResult.getShift(), result.getShift());
        assertEquals(reservationDTOResult.getReservationStatus(), result.getReservationStatus());
        assertEquals(reservationDTOResult.getClientDTO().getId(), result.getClientDTO().getId());

        verify(converter, times(1)).createReservationDTOFromReservation(savedReservation);
    }

    @Test
    void createReservation_ClientNotFound_ThrowsException() {
        NewReservationDTO reservationDTO = new NewReservationDTO();
        reservationDTO.setClientDTO(new ClientDTO(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(reservationDTO));
    }

    @Test
    void createReservation_RecordNotFound_ThrowsException() {
        Client client = new Client();
        client.setId(1L);

        NewReservationDTO reservationDTO = new NewReservationDTO();
        reservationDTO.setClientDTO(new ClientDTO(1L));
        reservationDTO.setDinners(4);
        reservationDTO.setReservationDate(LocalDate.now().plusDays(1));
        reservationDTO.setShift(Shift.DAY1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.empty());

        assertThrows(IllegalCalendarException.class, () -> reservationService.createReservation(reservationDTO));
    }

    @Test
    void createReservation_NotPossible_ThrowsException() {

        Client client = new Client();
        client.setId(1L);

        NewReservationDTO reservationDTO = new NewReservationDTO();
        reservationDTO.setClientDTO(new ClientDTO(1L));
        reservationDTO.setDinners(4);
        reservationDTO.setReservationDate(LocalDate.now().plusDays(1));
        reservationDTO.setShift(Shift.DAY1);

        ReservationRecord record = new ReservationRecord(reservationDTO.getReservationDate(), reservationDTO.getShift());
        record.setEmptySpaces(0); // Set to zero to simulate no available spaces

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(record));

        ReservationException exception = assertThrows(ReservationException.class, () -> reservationService.createReservation(reservationDTO));

        assertEquals("Reservation is not possible due to lack of available spaces.", exception.getMessage());
    }

    @Test
    void createReservation_ClientNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Client not found with id: " + reservationDTO.getClientDTO().getId();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createReservation_NotPossibleDueToDinners() {
        reservationDTO.setDinners(7);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(reservationRecord));

        Exception exception = assertThrows(ReservationException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Reservation is not possible due the amount of dinners.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createReservation_ReservationRecordNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalCalendarException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Calendar not found with with date " + reservationDTO.getReservationDate() + " and shift " + reservationDTO.getShift();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createReservation_NotPossibleDueToLackOfSpaces() {
        reservationRecord.setEmptySpaces(2); // Not enough spaces
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(reservationRecord));

        Exception exception = assertThrows(ReservationException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Reservation is not possible due to lack of available spaces.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createReservation_NotPossibleDueToPastDate() {
        reservationDTO.setReservationDate(LocalDate.now().minusDays(1)); // Past date
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(reservationRecord));

        Exception exception = assertThrows(ReservationException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Couldn't made a reservation on a passed day";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createReservation_NotPossibleDueToExistingReservation() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.of(reservationRecord));
        when(reservationRepository.findByClientAndReservationDateAndShift(any(Client.class), any(LocalDate.class), any(Shift.class)))
                .thenReturn(Optional.of(savedReservation));

        Exception exception = assertThrows(ReservationException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        String expectedMessage = "Clients couldn't had different reservation for the same day and shift";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getReservationsByClient_HappyPath() {
        when(reservationRepository.findAllByClient(any(Client.class))).thenReturn(List.of(savedReservation));

        List<ReservationDTO> result = reservationService.getReservationsByClient(client);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationDTOResult.getId(), result.get(0).getId());
    }

    @Test
    void getReservationsByClient_NoReservations() {
        when(reservationRepository.findAllByClient(any(Client.class))).thenReturn(List.of());

        List<ReservationDTO> result = reservationService.getReservationsByClient(client);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getClientFromAuthentication_HappyPath() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(client));

        Client result = reservationService.getClientFromAuthentication(authentication);

        assertNotNull(result);
        assertEquals(client.getId(), result.getId());
    }

    @Test
    void getClientFromAuthentication_AuthenticationNull() {
        assertThrows(CustomAuthenticationException.class, () -> {
            reservationService.getClientFromAuthentication(null);
        });
    }

    @Test
    void getClientFromAuthentication_PrincipalNull() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(CustomAuthenticationException.class, () -> {
            reservationService.getClientFromAuthentication(authentication);
        });
    }

    @Test
    void getClientFromAuthentication_EmailNotFound() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> {
            reservationService.getClientFromAuthentication(authentication);
        });
    }

    @Test
    void getClientFromAuthentication_PrincipalNotRecognized() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(CustomAuthenticationException.class, () -> {
            reservationService.getClientFromAuthentication(authentication);
        });
    }

    @Test
    void getAcceptedReservationsByClient_HappyPath() {
        when(reservationRepository.findAllByClientAndReservationStatus(any(Client.class), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(List.of(savedReservation));

        List<ReservationDTO> result = reservationService.getAcceptedReservationsByClient(client);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationDTOResult.getId(), result.get(0).getId());
    }

    @Test
    void getAcceptedReservationsByClient_NoAcceptedReservations() {
        when(reservationRepository.findAllByClientAndReservationStatus(any(Client.class), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(List.of());

        List<ReservationDTO> result = reservationService.getAcceptedReservationsByClient(client);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getReservationById_HappyPath() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        ReservationDTO result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(reservationDTOResult.getId(), result.getId());
    }

    @Test
    void getReservationById_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.getReservationById(1L);
        });
    }

    @Test
    void getAllReservationsForDay_HappyPath() {
        when(reservationRepository.findAllByReservationDate(any(LocalDate.class))).thenReturn(List.of(savedReservation));

        List<ReservationDTO> result = reservationService.getAllReservationsForDay(LocalDate.now().plusDays(1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationDTOResult.getId(), result.get(0).getId());
    }

    @Test
    void getAllReservationsForDay_NoReservations() {
        when(reservationRepository.findAllByReservationDate(any(LocalDate.class))).thenReturn(List.of());

        List<ReservationDTO> result = reservationService.getAllReservationsForDay(LocalDate.now().plusDays(1));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllReservationsForDayAndShift_HappyPath() {
        when(reservationRepository.findAllByReservationDateAndShift(any(LocalDate.class), any(Shift.class))).thenReturn(List.of(savedReservation));

        List<ReservationDTO> result = reservationService.getAllReservationsForDayAndShift(LocalDate.now().plusDays(1), Shift.DAY1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationDTOResult.getId(), result.get(0).getId());
    }

    @Test
    void getAllReservationsForDayAndShift_NoReservations() {
        when(reservationRepository.findAllByReservationDateAndShift(any(LocalDate.class), any(Shift.class))).thenReturn(List.of());

        List<ReservationDTO> result = reservationService.getAllReservationsForDayAndShift(LocalDate.now().plusDays(1), Shift.DAY1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void cancelReservation_HappyPath() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationRecordRepository.findByReservationDateAndShift(any(LocalDate.class), any(Shift.class)))
                .thenReturn(Optional.of(reservationRecord));
        when(reservationRepository.findAllByReservationDateAndShift(any(LocalDate.class), any(Shift.class)))
                .thenReturn(List.of(savedReservation));

        ReservationDTO result = reservationService.cancelReservation(1L, client);

        assertNotNull(result);
        assertEquals(reservationDTOResult.getId(), result.getId());
        assertEquals(ReservationStatus.CANCELED, result.getReservationStatus());

        verify(reservationRepository, times(1)).findById(anyLong());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(reservationRecordRepository, times(1)).findByReservationDateAndShift(any(LocalDate.class), any(Shift.class));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShift(any(LocalDate.class), any(Shift.class));
    }

    @Test
    void cancelReservation_NotFound() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.cancelReservation(1L, client);
        });
    }

    @Test
    void cancelReservation_NotAuthorized() {
        Client otherClient = new Client(2L, "Jane", "Doe", "jane.doe@example.com", "password123", true, new Role(1L, "ROLE_USER"));

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(1L, otherClient);
        });
    }

    @Test
    void cancelReservation_NotAccepted() {
        savedReservation.setReservationStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(1L, client);
        });
    }

    @Test
    void cancelReservation_SameDayCancellation() {
        savedReservation.setReservationDate(LocalDate.now());

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(1L, client);
        });
    }

    @Test
    void cancelReservation_PastReservationCancellation() {
        savedReservation.setReservationDate(LocalDate.now().minusDays(1));

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(1L, client);
        });
    }



    @Test
    void updateReservationStatus_HappyPath() {
        savedReservation.setReservationDate(LocalDate.now());
        LocalDate reservationDate = savedReservation.getReservationDate();
        LocalTime currentTime = LocalTime.now();

        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(reservationDate, currentTime, ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationDTO result = reservationService.updateReservationStatus(1L, dto);

        assertNotNull(result);
        assertEquals(ReservationStatus.CONFIRMED, result.getReservationStatus());
    }

    @Test
    void updateReservationStatus_NotFound() {
        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(LocalDate.now(), LocalTime.now(), ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.updateReservationStatus(1L, dto);
        });
    }

    @Test
    void updateReservationStatus_InvalidActionDate() {
        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(LocalDate.now().minusDays(1), LocalTime.now(), ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(1L, dto);
        });
    }

    @Test
    void updateReservationStatus_InvalidShiftForCurrentTime() {
        LocalDate today = LocalDate.now();
        LocalTime invalidTime = LocalTime.of(9, 0);

        savedReservation.setReservationDate(today);

        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(today, invalidTime, ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        ReservationException exception = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(1L, dto);
        });

        String expectedMessage = "This reservation cannot be confirmed at the current time.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void updateReservationStatus_NotAccepted() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        savedReservation.setReservationDate(today);
        savedReservation.setReservationStatus(ReservationStatus.CANCELED);

        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(today, currentTime, ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        ReservationException exception = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(1L, dto);
        });

        String expectedMessage = "Only accepted reservations can be confirmed.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateReservationStatus_InvalidStatusUpdate() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        savedReservation.setReservationDate(today);

        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(today, currentTime, ReservationStatus.ACCEPTED);

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(savedReservation));

        ReservationException exception = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(1L, dto);
        });

        String expectedMessage = "Reservations could only be updated to CONFIRMED or LOST status.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void updateReservationsToLoss_HappyPath() {
        LocalDate actionDate = LocalDate.now();
        LocalTime currentTime = LocalTime.of(23, 0); // Example time to allow multiple shifts
        List<Reservation> reservations = List.of(savedReservation);

        // Mock responses for each shift
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY1), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY2), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY3), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY4), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT1), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT2), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT3), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);
        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT4), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(reservations);

        when(reservationRepository.saveAll(anyList())).thenReturn(reservations);


        List<ReservationDTO> result = reservationService.updateReservationsToLoss(actionDate, currentTime);


        assertNotNull(result);
        assertEquals(8, result.size());
        for (ReservationDTO dto : result) {
            assertEquals(ReservationStatus.LOST, dto.getReservationStatus());
        }


        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY1), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY2), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY3), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.DAY4), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT1), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT2), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT3), eq(ReservationStatus.ACCEPTED));
        verify(reservationRepository, times(1)).findAllByReservationDateAndShiftAndReservationStatus(eq(actionDate), eq(Shift.NIGHT4), eq(ReservationStatus.ACCEPTED));
    }


    @Test
    void updateReservationsToLoss_InvalidActionDate() {
        LocalDate actionDate = LocalDate.now().minusDays(1);
        LocalTime currentTime = LocalTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationsToLoss(actionDate, currentTime);
        });
    }

    @Test
    void updateReservationsToLoss_NoReservationsToUpdate() {
        LocalDate actionDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        when(reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(any(LocalDate.class), any(Shift.class), eq(ReservationStatus.ACCEPTED)))
                .thenReturn(List.of());

        List<ReservationDTO> result = reservationService.updateReservationsToLoss(actionDate, currentTime);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}