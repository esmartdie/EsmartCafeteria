package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.ReservationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

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
    private IReservationRepository reservationRepository;

    @Mock
    private IReservationRecordRepository reservationRecordRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReservation_HappyPath() {

        Reservation reservation = new Reservation();
        reservation.setDinners(4);
        reservation.setShift(Shift.DAY1);
        reservation.setReservationDate(LocalDate.now());
        when(reservationRecordRepository.findByReservationDateAndShift(any(), any())).thenReturn(Optional.empty());
        when(reservationRepository.save(any())).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.setId(1L);
            return savedReservation;
        });

        Reservation createdReservation = reservationService.createReservation(reservation);

        assertNotNull(createdReservation);
        assertEquals(ReservationStatus.ACCEPTED, createdReservation.getReservationStatus());
    }

    @Test
    public void testCreateReservation_ExceedingMaximumDinners() {
        Reservation reservation = new Reservation();
        reservation.setDinners(7);

        assertThrows(ReservationException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    void testCreateReservation_ReservationNotPossible_LackOfAvailableSpaces() {
        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setEmptySpaces(0);
        Mockito.when(reservationRecordRepository.findByReservationDateAndShift(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(reservationRecord));

        Reservation reservation = new Reservation();
        reservation.setDinners(4);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setShift(Shift.DAY1);
        reservation.setReservationDate(LocalDate.now());


        ReservationException exception = assertThrows(ReservationException.class,
                () -> reservationService.createReservation(reservation));

        assertEquals("Reservation is not possible due to lack of available spaces.", exception.getMessage());
    }

    @Test
    void testCreateReservation_ReservationNotPossible_MinimumNotPossible() {
        Reservation reservation = new Reservation();
        reservation.setDinners(0);

        assertThrows(ReservationException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    void testGetReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepository.findByClient(client)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationService.getReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepository, times(1)).findByClient(client);
    }

    @Test
    void testGetAcceptedReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepository.findByClientAndReservationStatus(client, ReservationStatus.ACCEPTED))
                .thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationService.getAcceptedReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepository, times(1)).findByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
    }

    @Test
    void testGetReservationById() {
        Long id = 1L;
        Reservation expectedReservation = new Reservation();
        when(reservationRepository.findById(id)).thenReturn(Optional.of(expectedReservation));

        Optional<Reservation> result = reservationService.getReservationById(id);

        assertEquals(expectedReservation, result.orElse(null));
        verify(reservationRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllReservationsForDay() {
        LocalDate date = LocalDate.now();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepository.findByReservationDate(date)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationService.getAllReservationsForDay(date);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepository, times(1)).findByReservationDate(date);
    }

    @Test
    void testGetAllReservationsForDayAndShift() {
        LocalDate date = LocalDate.now();
        Shift shift = Shift.DAY4;
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepository.findByReservationDateAndShift(date, shift)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationService.getAllReservationsForDayAndShift(date, shift);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepository, times(1)).findByReservationDateAndShift(date, shift);
    }

}