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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;


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

        reservationRepository.deleteAll();
        userRepository.deleteAll();
        reservationRecordRepository.deleteAll();
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
        when(reservationRepositoryMock.findAllByClient(client)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findAllByClient(client);
    }

    @Test
    void testGetAcceptedReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED))
                .thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAcceptedReservationsByClient(client);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
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
        when(reservationRepositoryMock.findAllByReservationDate(date)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAllReservationsForDay(date);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findAllByReservationDate(date);
    }

    @Test
    void testGetAllReservationsForDayAndShift() {
        LocalDate date = LocalDate.now();
        Shift shift = Shift.DAY4;
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByReservationDateAndShift(date, shift)).thenReturn(Optional.of(expectedReservations));

        Optional<List<Reservation>> result = reservationServiceMock.getAllReservationsForDayAndShift(date, shift);

        assertEquals(expectedReservations, result.orElse(null));
        verify(reservationRepositoryMock, times(1)).findAllByReservationDateAndShift(date, shift);
    }


    @Test
    public void integrationTestCancelledAReserve_HappyPath() {

        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(dateAfter, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, dateAfter);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);

        Reservation cancelledReservation1 = reservationService.cancelReservation(savedReservation1.getId());

        assertEquals(ReservationStatus.CANCELED, reservationRepository.findById(savedReservation1.getId()).get().getReservationStatus());

    }

    @Test
    public void integrationTestCancelledAReserve_SadPathCancellationSameDayOfReservation() {

        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(date, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, date);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);

        Long reservationId = savedReservation1.getId();

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId);
        });

        String expectedMessage = "Cannot cancel a reservation on the same day.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void integrationTestCancelledAReserve_SadPathCancellationConfirmedReservation() {

        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(date, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, date);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);

        Long reservationId = savedReservation1.getId();

        Reservation reservationConfirmed = reservationService.confirmReservation(reservationId, date, LocalTime.now());

        assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservationId).get().getReservationStatus());

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId);
        });

        String expectedMessage = "This reservation couldn't be canceled";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void integrationTestCancelledAReserve_SadPathCancellationLossReservation() {

        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(date, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, date);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);

        Long reservationId = savedReservation1.getId();

        Reservation lostConfirmed = reservationService.lostReservation(reservationId, date, LocalTime.now());

        assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservationId).get().getReservationStatus());

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId);
        });

        String expectedMessage = "This reservation couldn't be canceled";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void confirmAReservationWhichStatusIsCancelled(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(date, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, date);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);
        updateReservationStatus(savedReservation1, ReservationStatus.CANCELED);

        Long reservationId = savedReservation1.getId();

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.confirmReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "This reservation couldn't updated to confirmed";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void updateToLostAReservationWhichStatusIsCancelled(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(date, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, date);
        Reservation savedReservation1 = reservationService.createReservation(reservation1);
        updateReservationStatus(savedReservation1, ReservationStatus.LOST);

        Long reservationId = savedReservation1.getId();

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.lostReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "This reservation couldn't updated to LOST";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void updateToConfirmedAReservationWhichOccursADayBefore(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(dateBefore, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, dateBefore);
        reservationRepository.save(reservation1);
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateBefore).stream().findFirst().get().getFirst();
        updateReservationStatus(savedReservation1, ReservationStatus.ACCEPTED);

        Long reservationId = savedReservation1.getId();

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.confirmReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "Reservations can only be updated to 'CONFIRMED' if the action is performed on the same day.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void updateToConfirmedAFutureReservation(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(dateAfter, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, dateAfter);
        reservationRepository.save(reservation1);
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateAfter).stream().findFirst().get().getFirst();
        updateReservationStatus(savedReservation1, ReservationStatus.ACCEPTED);

        Long reservationId = savedReservation1.getId();

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.confirmReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "Reservations can only be updated to 'CONFIRMED' if the action is performed on the same day.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void updateToLostAReservationWhichOccursADayBefore(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(dateBefore, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, dateBefore);
        reservationRepository.save(reservation1);
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateBefore).stream().findFirst().get().getFirst();
        updateReservationStatus(savedReservation1, ReservationStatus.ACCEPTED);

        Long reservationId = savedReservation1.getId();

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.lostReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "Reservations can only be updated to 'LOSS' if the action is performed on the same day.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    public void updateToLostAReservationWhichOccursADayAfter(){
        Client client = new Client();
        userRepository.save(client);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;

        ReservationRecord reservationRecord = createReservationRecord(dateAfter, shift, 40);
        reservationRecordRepository.save(reservationRecord);

        Reservation reservation1 = createReservation(client, shift, dateAfter);
        reservationRepository.save(reservation1);
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateAfter).stream().findFirst().get().getFirst();
        updateReservationStatus(savedReservation1, ReservationStatus.ACCEPTED);

        Long reservationId = savedReservation1.getId();

        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.lostReservation(reservationId, date, LocalTime.now());
        });

        String expectedMessage = "Reservations can only be updated to 'LOSS' if the action is performed on the same day.";
        String actualMessage = thrownException.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }
    @Test
    public void massiveUpdateReservationsToLost(){
        Client client = new Client();
        userRepository.save(client);

        Client client2 = new Client();
        userRepository.save(client2);

        LocalDate date = LocalDate.now();
        LocalDate dateBefore = date.minusDays(1);
        LocalDate dateAfter = date.plusDays(1);
        int emptySpaces = 40;
        Shift shift = Shift.DAY3;
        LocalTime actualTime = LocalTime.now();

        ReservationRecord reservationRecord = createReservationRecord(date, Shift.DAY1, 40);
        reservationRecordRepository.save(reservationRecord);
        ReservationRecord reservationRecord2 = createReservationRecord(date, Shift.DAY2, 40);
        reservationRecordRepository.save(reservationRecord2);
        ReservationRecord reservationRecord3 = createReservationRecord(date, Shift.DAY3, 40);
        reservationRecordRepository.save(reservationRecord3);
        ReservationRecord reservationRecord4 = createReservationRecord(date, Shift.DAY4, 40);
        reservationRecordRepository.save(reservationRecord4);
        ReservationRecord reservationRecord5 = createReservationRecord(date, Shift.NIGHT1, 40);
        reservationRecordRepository.save(reservationRecord5);
        ReservationRecord reservationRecord6 = createReservationRecord(date, Shift.NIGHT2, 40);
        reservationRecordRepository.save(reservationRecord6);
        ReservationRecord reservationRecord7 = createReservationRecord(date, Shift.NIGHT3, 40);
        reservationRecordRepository.save(reservationRecord7);
        ReservationRecord reservationRecord8 = createReservationRecord(date, Shift.NIGHT4, 40);
        reservationRecordRepository.save(reservationRecord8);
        ReservationRecord reservationRecord9 = createReservationRecord(dateAfter, Shift.NIGHT4, 40);
        reservationRecordRepository.save(reservationRecord9);
        ReservationRecord reservationRecord10 = createReservationRecord(dateBefore, Shift.NIGHT4, 40);
        reservationRecordRepository.save(reservationRecord10);

        Reservation reservation1 = reservationService.createReservation(createReservation(client, Shift.DAY1, date));
        Reservation reservation2 = reservationService.createReservation(createReservation(client2, Shift.DAY1, date));
        Reservation reservation3 = reservationService.createReservation(createReservation(client, Shift.DAY2, date));
        Reservation reservation4 = reservationService.createReservation(createReservation(client2, Shift.DAY2, date));
        Reservation reservation5 = reservationService.createReservation(createReservation(client, Shift.DAY3, date));
        Reservation reservation6 = reservationService.createReservation(createReservation(client2, Shift.DAY3, date));
        Reservation reservation7 = reservationService.createReservation(createReservation(client, Shift.DAY4, date));
        Reservation reservation8 = reservationService.createReservation(createReservation(client2, Shift.DAY4, date));
        Reservation reservation9 = reservationService.createReservation(createReservation(client, Shift.NIGHT1, date));
        Reservation reservation10 = reservationService.createReservation(createReservation(client2, Shift.NIGHT1, date));
        Reservation reservation11 = reservationService.createReservation(createReservation(client, Shift.NIGHT2, date));
        Reservation reservation12 = reservationService.createReservation(createReservation(client2, Shift.NIGHT2, date));
        Reservation reservation13 = reservationService.createReservation(createReservation(client, Shift.NIGHT3, date));
        Reservation reservation14 = reservationService.createReservation(createReservation(client2, Shift.NIGHT3, date));
        Reservation reservation15 = reservationService.createReservation(createReservation(client, Shift.NIGHT4, date));
        Reservation reservation16 = reservationService.createReservation(createReservation(client2, Shift.NIGHT4, date));
        Reservation reservation17 = reservationService.createReservation(createReservation(client, Shift.NIGHT4, dateAfter));
        Reservation reservation18 = reservationService.createReservation(createReservation(client2, Shift.NIGHT4, dateAfter));
        Reservation reservation19 = createReservation(client, Shift.NIGHT4, dateBefore);
        reservation19.setReservationStatus(ReservationStatus.ACCEPTED);
        Reservation reservation20 = createReservation(client2, Shift.NIGHT4, dateBefore);
        reservation20.setReservationStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation19);
        reservationRepository.save(reservation20);

        updateReservationStatus(reservation1, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation3, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation5, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation7, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation9, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation11, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation13, ReservationStatus.CONFIRMED);
        updateReservationStatus(reservation15, ReservationStatus.CONFIRMED);

        reservationService.updateReservationsToLoss(date, LocalTime.now());

        Map<LocalTime, Consumer<Reservation>> testCases = new LinkedHashMap<>();

        testCases.put(LocalTime.of(13, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation1.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation2.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(14, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation3.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation4.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(15, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation5.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation6.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(16, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation7.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation8.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(20, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation9.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation10.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(21, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation11.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation12.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(22, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation13.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation14.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });
        testCases.put(LocalTime.of(23, 0), (reservation) -> {
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation15.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservation16.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation17.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation18.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.ACCEPTED, reservationRepository.findById(reservation19.getId()).get().getReservationStatus());
            assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservation20.getId()).get().getReservationStatus());
        });


        for (Map.Entry<LocalTime, Consumer<Reservation>> entry : testCases.entrySet()) {
            if (actualTime.isAfter(entry.getKey())) {
                entry.getValue().accept(null);
            }
        }
    }



    private Reservation createReservation(Client client, Shift shift, LocalDate date) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDinners(2);
        reservation.setReservationDate(date);
        reservation.setShift(shift);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setRecord(reservationRecordRepository.findByReservationDateAndShift(date, shift).get());
        return reservation;
    }

    private ReservationRecord createReservationRecord(LocalDate date, Shift shift, int emptySpaces){

        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setReservationDate(date);
        reservationRecord.setShift(shift);
        reservationRecord.setEmptySpaces(emptySpaces);
        reservationRecord.setReservationList(new ArrayList<>());

        return reservationRecord;
    }

    public void updateReservationStatus(Reservation reservation, ReservationStatus status){
       if(reservation.getId()!=null){
           reservation.setReservationStatus(status);
           reservationRepository.save(reservation);
       }
    }




}