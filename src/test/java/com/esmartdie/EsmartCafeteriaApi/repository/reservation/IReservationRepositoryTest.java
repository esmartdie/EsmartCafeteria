package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class IReservationRepositoryTest {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    private Client client;

    @BeforeEach
    public void setUp() {
        client = new Client();
        userRepository.save(client);
    }

    @AfterEach
    public void tearDown() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        Reservation reservation = createReservation(client, Shift.NIGHT2);
        Reservation savedReservation = reservationRepository.save(reservation);

        Optional<Reservation> retrievedReservationOptional = reservationRepository.findById(savedReservation.getId());

        assertTrue(retrievedReservationOptional.isPresent());
        assertEquals(savedReservation, retrievedReservationOptional.get());
    }

    @Test
    public void testFindByClient() {
        Reservation reservation1 = createReservation(client, Shift.DAY3);
        Reservation reservation2 = createReservation(client, Shift.DAY4);
        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> retrievedReservations = reservationRepository.findAllByClient(client);

        assertEquals(2, retrievedReservations.size());
        assertTrue(retrievedReservations.containsAll(List.of(reservation1, reservation2)));
    }

    @Test
    public void testFindByClientAndReservationStatus() {
        Reservation reservation1 = createReservation(client, Shift.NIGHT1);
        Reservation reservation2 = createReservation(client, Shift.NIGHT4);
        reservation2.setReservationStatus(ReservationStatus.CONFIRMED);
        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> retrievedReservations = reservationRepository.findAllByClientAndReservationStatus(client, ReservationStatus.CONFIRMED);

        assertEquals(1, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation2));
    }

    @Test
    public void testFindByReservationDate() {
        Reservation reservation1 = createReservation(client, Shift.NIGHT2);
        Reservation reservation2 = createReservation(client, Shift.DAY2);
        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> retrievedReservations = reservationRepository.findAllByReservationDate(LocalDate.of(2024, 5, 11));

        assertEquals(2, retrievedReservations.size());
        assertTrue(retrievedReservations.containsAll(List.of(reservation1, reservation2)));
    }

    @Test
    public void testFindByReservationDateAndShift() {
        Reservation reservation1 = createReservation(client, Shift.DAY1);
        Reservation reservation2 = createReservation(client, Shift.NIGHT1);
        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> retrievedReservations =
                reservationRepository.findAllByReservationDateAndShift(LocalDate.of(2024, 5, 11), Shift.DAY1);

        assertEquals(1, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation1));
    }

    @Test
    public void testFindByReservationDateAndShiftAndReservationStatus() {
        Reservation reservation1 = createReservation(client, Shift.DAY1);
        reservation1.setReservationStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation1);

        Reservation reservation2 = createReservation(client, Shift.DAY2);
        reservation2.setReservationDate(LocalDate.now());
        reservation2.setReservationStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation2);

        List<Reservation> reservations = reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(
                LocalDate.now(), Shift.DAY2, ReservationStatus.CONFIRMED);

        assertEquals(1, reservations.size());
        assertEquals(reservation2, reservations.get(0));
    }

    private Reservation createReservation(Client client, Shift shift) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDinners(2);
        reservation.setReservationDate(LocalDate.of(2024, 5, 11));
        reservation.setShift(shift);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservation;
    }
}