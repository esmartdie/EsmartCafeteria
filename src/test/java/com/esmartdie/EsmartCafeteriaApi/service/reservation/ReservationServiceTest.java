package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationException;
import com.esmartdie.EsmartCafeteriaApi.exception.ReservationNotFoundException;
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation.getClient(),
                reservation.getDinners(),
                reservation.getReservationDate(),
                reservation.getShift()
        );

        ReservationDTO createdReservation = reservationServiceMock.createReservation(newReservationDTO);

        assertNotNull(createdReservation);
        assertEquals(ReservationStatus.ACCEPTED, createdReservation.getReservationStatus());
    }

    @Test
    public void testCreateReservation_ExceedingMaximumDinners() {
        Reservation reservation = new Reservation();
        reservation.setDinners(7);

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation.getClient(),
                reservation.getDinners(),
                reservation.getReservationDate(),
                reservation.getShift()
        );

        assertThrows(ReservationException.class, () -> reservationServiceMock.createReservation(newReservationDTO));
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation.getClient(),
                reservation.getDinners(),
                reservation.getReservationDate(),
                reservation.getShift()
        );


        ReservationException exception = assertThrows(ReservationException.class,
                () -> reservationServiceMock.createReservation(newReservationDTO));

        assertEquals("Reservation is not possible due to lack of available spaces.", exception.getMessage());
    }

    @Test
    void testCreateReservation_ReservationNotPossible_MinimumNotPossible() {
        Reservation reservation = new Reservation();
        reservation.setDinners(0);

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation.getClient(),
                reservation.getDinners(),
                reservation.getReservationDate(),
                reservation.getShift()
        );


        assertThrows(ReservationException.class, () -> reservationServiceMock.createReservation(newReservationDTO));
    }

    @Test
    void testGetReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByClient(client)).thenReturn((expectedReservations));

        List<ReservationDTO> result = reservationServiceMock.getReservationsByClient(client);

        assertEquals(expectedReservations.get(0).getClient().getName(), result.get(0).getClientDTO().getName());
        verify(reservationRepositoryMock, times(1)).findAllByClient(client);
    }

    @Test
    void testGetAcceptedReservationsByClient() {
        Client client = new Client();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED))
                .thenReturn(expectedReservations);

        List<ReservationDTO> result = reservationServiceMock.getAcceptedReservationsByClient(client);

        assertEquals(expectedReservations.get(0).getReservationStatus(), result.get(0).getReservationStatus());
        verify(reservationRepositoryMock, times(1)).findAllByClientAndReservationStatus(client, ReservationStatus.ACCEPTED);
    }

    @Test
    void testGetReservationById() {
        Long id = 1L;
        Reservation expectedReservation = new Reservation();
        when(reservationRepositoryMock.findById(id)).thenReturn(Optional.of(expectedReservation));

        ReservationDTO result = reservationServiceMock.getReservationById(id);

        assertEquals(expectedReservation.getId(), result.getId());
        verify(reservationRepositoryMock, times(1)).findById(id);
    }

    @Test
    void testGetAllReservationsForDay() {
        LocalDate date = LocalDate.now();
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByReservationDate(date)).thenReturn((expectedReservations));

        List<ReservationDTO> result = reservationServiceMock.getAllReservationsForDay(date);

        assertEquals(expectedReservations.size(), result.size());
        verify(reservationRepositoryMock, times(1)).findAllByReservationDate(date);
    }

    @Test
    void testGetAllReservationsForDayAndShift() {
        LocalDate date = LocalDate.now();
        Shift shift = Shift.DAY4;
        List<Reservation> expectedReservations = new ArrayList<>();
        when(reservationRepositoryMock.findAllByReservationDateAndShift(date, shift)).thenReturn((expectedReservations));

       List<ReservationDTO> result = reservationServiceMock.getAllReservationsForDayAndShift(date, shift);

        assertEquals(expectedReservations.size(), result.size());
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );

        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);

        ReservationDTO cancelledReservation1 = reservationService.cancelReservation(savedReservation1.getId(), reservation1.getClient());


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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );

        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);

        Long reservationId = savedReservation1.getId();

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId,client);
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );

        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);


        ReservationDTO reservationConfirmed = reservationService.updateReservationStatus(reservationId, reservationDTO);

        assertEquals(ReservationStatus.CONFIRMED, reservationRepository.findById(reservationId).get().getReservationStatus());

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId, client);
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );

        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.LOST);

        ReservationDTO lostConfirmed = reservationService.updateReservationStatus(reservationId, reservationDTO);

        assertEquals(ReservationStatus.LOST, reservationRepository.findById(reservationId).get().getReservationStatus());

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.cancelReservation(reservationId, client);
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );

        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);

        updateReservationStatus(savedReservation1, ReservationStatus.CANCELED);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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

        NewReservationDTO newReservationDTO = new NewReservationDTO(
                reservation1.getClient(),
                reservation1.getDinners(),
                reservation1.getReservationDate(),
                reservation1.getShift()
        );


        ReservationDTO savedReservation1 = reservationService.createReservation(newReservationDTO);
        updateReservationStatus(savedReservation1, ReservationStatus.LOST);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.LOST);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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

        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateBefore).stream().findFirst().get();


        updateReservationStatus(convertToReservationDTO(savedReservation1), ReservationStatus.CONFIRMED);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateAfter).stream().findFirst().get();

        updateReservationStatus(convertToReservationDTO(savedReservation1), ReservationStatus.CONFIRMED);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateBefore).stream().findFirst().get();

        updateReservationStatus(convertToReservationDTO(savedReservation1), ReservationStatus.LOST);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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
        Reservation savedReservation1 = reservationRepository.findAllByReservationDate(dateAfter).stream().findFirst().get();
        updateReservationStatus(convertToReservationDTO(savedReservation1), ReservationStatus.LOST);

        Long reservationId = savedReservation1.getId();

        ReservationStatusUpdatedDTO reservationDTO = new ReservationStatusUpdatedDTO(date, LocalTime.now(), ReservationStatus.CONFIRMED);

        ReservationException thrownException = assertThrows(ReservationException.class, () -> {
            reservationService.updateReservationStatus(reservationId, reservationDTO);;
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

        ReservationDTO reservation1 = reservationService.createReservation(createReservationDTO(client, Shift.DAY1, date));
        ReservationDTO reservation2 = reservationService.createReservation(createReservationDTO(client2, Shift.DAY1, date));
        ReservationDTO reservation3 = reservationService.createReservation(createReservationDTO(client, Shift.DAY2, date));
        ReservationDTO reservation4 = reservationService.createReservation(createReservationDTO(client2, Shift.DAY2, date));
        ReservationDTO reservation5 = reservationService.createReservation(createReservationDTO(client, Shift.DAY3, date));
        ReservationDTO reservation6 = reservationService.createReservation(createReservationDTO(client2, Shift.DAY3, date));
        ReservationDTO reservation7 = reservationService.createReservation(createReservationDTO(client, Shift.DAY4, date));
        ReservationDTO reservation8 = reservationService.createReservation(createReservationDTO(client2, Shift.DAY4, date));
        ReservationDTO reservation9 = reservationService.createReservation(createReservationDTO(client, Shift.NIGHT1, date));
        ReservationDTO reservation10 = reservationService.createReservation(createReservationDTO(client2, Shift.NIGHT1, date));
        ReservationDTO reservation11 = reservationService.createReservation(createReservationDTO(client, Shift.NIGHT2, date));
        ReservationDTO reservation12 = reservationService.createReservation(createReservationDTO(client2, Shift.NIGHT2, date));
        ReservationDTO reservation13 = reservationService.createReservation(createReservationDTO(client, Shift.NIGHT3, date));
        ReservationDTO reservation14 = reservationService.createReservation(createReservationDTO(client2, Shift.NIGHT3, date));
        ReservationDTO reservation15 = reservationService.createReservation(createReservationDTO(client, Shift.NIGHT4, date));
        ReservationDTO reservation16 = reservationService.createReservation(createReservationDTO(client2, Shift.NIGHT4, date));
        ReservationDTO reservation17 = reservationService.createReservation(createReservationDTO(client, Shift.NIGHT4, dateAfter));
        ReservationDTO reservation18 = reservationService.createReservation(createReservationDTO(client2, Shift.NIGHT4, dateAfter));
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

    private NewReservationDTO createReservationDTO (Client client, Shift shift, LocalDate date) {
        NewReservationDTO reservation = new NewReservationDTO();
        reservation.setClient(client);
        reservation.setDinners(2);
        reservation.setReservationDate(date);
        reservation.setShift(shift);
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

    public void updateReservationStatus(ReservationDTO reservationDTO, ReservationStatus status){
        Client client = (Client)userRepository.findByEmail(reservationDTO.getClientDTO().getEmail()).get();
        Reservation reservation = new  Reservation(
                client,
                reservationDTO.getDinners(),
                reservationRecordRepository.findByReservationDateAndShift(reservationDTO.getReservationDate(), reservationDTO.getShift()).get(),
                reservationDTO.getReservationDate(),
                reservationDTO.getShift());

       if(reservation.getId()!=null){
           reservation.setReservationStatus(status);

           reservationRepository.save(reservation);
       }
    }

    private ReservationDTO convertToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDinners(reservation.getDinners());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setShift(reservation.getShift());
        dto.setReservationStatus(reservation.getReservationStatus());


        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName(reservation.getClient().getName());
        clientDTO.setLastName(reservation.getClient().getLastName());
        clientDTO.setEmail(reservation.getClient().getEmail());
        clientDTO.setActive(reservation.getClient().getActive());
        clientDTO.setRating(reservation.getClient().getRating());

        dto.setClientDTO(clientDTO);
        return dto;
    }




}