package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.exception.*;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.*;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private DTOConverter converter;

    private Client client;
    private ReservationRecord reservationRecord;
    private NewReservationDTO reservationDTO;
    private Reservation savedReservation;
    private ReservationDTO reservationDTOResult;
    private Role role;

    @BeforeEach
    void setUp() {

        role = new Role(null, "ROLE_USER");
        role=roleRepository.save(role);


        client = new Client(null,"John", "Doe", "john.doe@example.com", "password123", true, role);
        client.setRating(5.0);
        client=userRepository.save(client);


        reservationRecord = new ReservationRecord(LocalDate.now().plusDays(1), Shift.DAY1);
        reservationRecord.setEmptySpaces(10);
        reservationRecord=reservationRecordRepository.save(reservationRecord);


        ClientDTO clientDTO = new ClientDTO(client.getId(), "John", "Doe", "john.doe@example.com", true);
        reservationDTO = new NewReservationDTO();
        reservationDTO.setClientDTO(clientDTO);
        reservationDTO.setDinners(4);
        reservationDTO.setReservationDate(LocalDate.now().plusDays(1));
        reservationDTO.setShift(Shift.DAY1);


        savedReservation = new Reservation(client, 4, reservationRecord, LocalDate.now().plusDays(1), Shift.DAY1);
        savedReservation.setId(1L);
        savedReservation.setReservationStatus(ReservationStatus.ACCEPTED);


        reservationDTOResult = converter.createReservationDTOFromReservation(savedReservation);
    }

    @AfterEach
    void tearDown(){
        reservationRepository.deleteAll();
        reservationRecordRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }


    @Test
    void createReservation_HappyPath() {
        ReservationDTO result = reservationService.createReservation(reservationDTO);

        assertNotNull(result);
        assertEquals(reservationDTOResult.getDinners(), result.getDinners());
        assertEquals(reservationDTOResult.getReservationDate(), result.getReservationDate());
        assertEquals(reservationDTOResult.getShift(), result.getShift());
        assertEquals(reservationDTOResult.getReservationStatus(), result.getReservationStatus());
        assertEquals(reservationDTOResult.getClientDTO().getId(), result.getClientDTO().getId());
    }


    @Test
    void createReservation_ClientNotFound() {
        reservationDTO.getClientDTO().setId(999L);

        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(reservationDTO));
    }


    @Test
    void createReservation_RecordNotFound() {
        reservationDTO.setReservationDate(LocalDate.now().plusDays(2)); // Non-existent record

        assertThrows(IllegalCalendarException.class, () -> reservationService.createReservation(reservationDTO));
    }


    @Test
    void createReservation_NotPossibleDueToLackOfSpaces() {
        reservationRecord.setEmptySpaces(2);
        reservationRecordRepository.save(reservationRecord);

        assertThrows(ReservationException.class, () -> reservationService.createReservation(reservationDTO));
    }


    @Test
    void cancelReservation_HappyPath() {
        savedReservation=reservationRepository.save(savedReservation);
        ReservationDTO result = reservationService.cancelReservation(savedReservation.getId(), client);

        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELED, result.getReservationStatus());
    }


    @Test
    void cancelReservation_NotFound() {
        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(999L, client));
    }


    @Test
    void cancelReservation_NotAuthorized() {
        savedReservation=reservationRepository.save(savedReservation);
        Client otherClient = new Client(2L, "Jane", "Doe", "jane.doe@example.com", "password123", true, role);
        userRepository.save(otherClient);

        assertThrows(ReservationException.class, () -> reservationService.cancelReservation(savedReservation.getId(), otherClient));
    }


    @Test
    void cancelReservation_NotAccepted() {
        savedReservation.setReservationStatus(ReservationStatus.CONFIRMED);
        savedReservation=reservationRepository.save(savedReservation);

        assertThrows(ReservationException.class, () -> reservationService.cancelReservation(savedReservation.getId(), client));
    }


    @Test
    void cancelReservation_SameDayCancellation() {
        savedReservation.setReservationDate(LocalDate.now());
        savedReservation=reservationRepository.save(savedReservation);

        assertThrows(ReservationException.class, () -> reservationService.cancelReservation(savedReservation.getId(), client));
    }



    @Test
    void updateReservationStatus_NotFound() {
        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(LocalDate.now(), LocalTime.now(), ReservationStatus.CONFIRMED);

        assertThrows(ReservationNotFoundException.class, () -> reservationService.updateReservationStatus(999L, dto));
    }


    @Test
    void updateReservationStatus_InvalidActionDate() {
        savedReservation=reservationRepository.save(savedReservation);
        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(LocalDate.now().minusDays(1), LocalTime.now(), ReservationStatus.CONFIRMED);

        assertThrows(IllegalArgumentException.class, () -> reservationService.updateReservationStatus(savedReservation.getId(), dto));
    }


    @Test
    void updateReservationStatus_InvalidShiftForCurrentTime() {
        LocalDate today = LocalDate.now();
        LocalTime invalidTime = LocalTime.of(9, 0);

        savedReservation.setReservationDate(today);
        savedReservation=reservationRepository.save(savedReservation);

        ReservationStatusUpdatedDTO dto = new ReservationStatusUpdatedDTO(today, invalidTime, ReservationStatus.CONFIRMED);

        ReservationException exception = assertThrows(ReservationException.class, () -> reservationService.updateReservationStatus(savedReservation.getId(), dto));

        String expectedMessage = "This reservation cannot be confirmed at the current time.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}

