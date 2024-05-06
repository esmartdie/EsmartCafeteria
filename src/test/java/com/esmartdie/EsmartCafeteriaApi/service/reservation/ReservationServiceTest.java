package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationException;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReservationServiceTest {

    @Mock
    private IReservationRepository reservationRepositoryMock;

    @Mock
    private IReservationRecordRepository reservationRecordRepositoryMock;

    @InjectMocks
    private ReservationService reservationServiceMock;


    @Mock
    private ReservationNotFoundException reservationNotFoundException;

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown(){
/*
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        reservationRecordRepository.deleteAll();


 */
    }

    @Test
    public void testCreateReservation_HappyPath() {

        Reservation reservation = new Reservation();
        reservation.setDinners(4);
        reservation.setShift(Shift.DAY1);
        reservation.setReservationDate(LocalDate.now());
        when(reservationRecordRepositoryMock.findByReservationDateAndShift(any(), any())).thenReturn(Optional.empty());
        when(reservationRepositoryMock.save(any())).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.setId(1L);
            return savedReservation;
        });

        Reservation createdReservation = reservationServiceMock.createReservation(reservation);

        assertNotNull(createdReservation);
        assertEquals(ReservationStatus.ACCEPTED, createdReservation.getReservationStatus());
    }

    @Test
    public void testCreateReservation_ExceedingMaximumDinners() {
        Reservation reservation = new Reservation();
        reservation.setDinners(7);

        assertThrows(ReservationException.class, () -> reservationServiceMock.createReservation(reservation));
    }

    @Test
    void testCreateReservation_ReservationNotPossible_LackOfAvailableSpaces() {
        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setEmptySpaces(0);
        Mockito.when(reservationRecordRepositoryMock.findByReservationDateAndShift(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(reservationRecord));

        Reservation reservation = new Reservation();
        reservation.setDinners(4);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setShift(Shift.DAY1);
        reservation.setReservationDate(LocalDate.now());


        ReservationException exception = assertThrows(ReservationException.class,
                () -> reservationServiceMock.createReservation(reservation));

        assertEquals("Reservation is not possible due to lack of available spaces.", exception.getMessage());
    }

    @Test
    void testCreateReservation_ReservationNotPossible_MinimumNotPossible() {
        Reservation reservation = new Reservation();
        reservation.setDinners(0);

        assertThrows(ReservationException.class, () -> reservationServiceMock.createReservation(reservation));
    }

    @Test
    void testGetReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findByClient(client)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findByClient(client);
    }

    @Test
    void testGetAcceptedReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findByClientAndReservationStatus(client, ReservationStatus.ACCEPTED))
                .thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAcceptedReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
    }

    @Test
    void testGetReservationById() {
        Long id = 1L;
        Reservation expectedReservation = new Reservation();
        when(reservationRepositoryMock.findById(id)).thenReturn(Optional.of(expectedReservation));

        Optional<Reservation> result = reservationServiceMock.getReservationById(id);

        assertEquals(expectedReservation, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findById(id);
    }

    @Test
    void testGetAllReservationsForDay() {
        LocalDate date = LocalDate.now();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findByReservationDate(date)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAllReservationsForDay(date);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findByReservationDate(date);
    }

    @Test
    void testGetAllReservationsForDayAndShift() {
        LocalDate date = LocalDate.now();
        Shift shift = Shift.DAY4;
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findByReservationDateAndShift(date, shift)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAllReservationsForDayAndShift(date, shift);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findByReservationDateAndShift(date, shift);
    }


    @Test
    public void integrationTestCancelledAReserve() {

        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 = createReservation(client, Shift.DAY1);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);

        Reservation reservation2 = createReservation(client, Shift.DAY1);
        Reservation savedReservation2 = reservationService.createReservation(reservation2);

        Reservation reservation3 = createReservation(client, Shift.DAY1);
        Reservation savedReservation3 = reservationService.createReservation(reservation3);





/*
        Long reservationIdToCancel = savedReservation3.getId();
        reservationService.cancelReservation(reservationIdToCancel);




        Optional<ReservationRecord> optionalReservationRecord =
                reservationRecordRepository.findByReservationDateAndShift(LocalDate.now(), Shift.DAY1);

        assertEquals(38, optionalReservationRecord.get().getEmptySpaces());

 */



        //List<Reservation> reservationList = reservationService.getAllReservationsForDay(LocalDate.of(2024, 05, 11)).get();


        //assertEquals(3, reservationList.size());
/*
        Reservation cancelledReservation = reservationService.cancelReservation(99L);

        assertEquals(ReservationStatus.CANCELED, cancelledReservation.getReservationStatus());


 */


    }

    private Reservation createReservation(Client client, Shift shift) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDinners(6);
        reservation.setReservationDate(LocalDate.of(2024, 5, 3));
        reservation.setShift(shift);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setRecord(reservationRecordRepository.findByReservationDateAndShift(reservation.getReservationDate(), shift).get());
        return reservation;
    }




}