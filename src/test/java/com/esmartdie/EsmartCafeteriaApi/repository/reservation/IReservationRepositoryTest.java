package com.esmartdie.EsmartCafeteriaApi.repository.reservation;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IReservationRepositoryTest {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    @AfterEach
    public void tearDown(){

        reservationRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    public void testFindById() {

        Client client = new Client();
        userRepository.save(client);

        Reservation reservation = createReservation(client, Shift.NIGHT2);
        Reservation savedReservation = reservationRepository.save(reservation);
        Optional<Reservation> retrievedReservationOptional = reservationRepository.findById(savedReservation.getId());
        Reservation reservation1 = retrievedReservationOptional.get();

        assertTrue(retrievedReservationOptional.isPresent());
        assertEquals(savedReservation, reservation1);
    }

    @Test
    public void testFindByClient() {

        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 = createReservation(client, Shift.DAY3);
        Reservation reservation2 = createReservation(client, Shift.DAY4);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

       List<Reservation> retrievedReservations = reservationRepository.findAllByClient(client);

        assertEquals(2, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation1));
        assertTrue(retrievedReservations.contains(reservation2));
    }

    @Test
    public void testFindByClientAndReservationStatus() {

        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 = createReservation(client, Shift.NIGHT1);
        Reservation reservation2 = createReservation(client,  Shift.NIGHT4);
        reservation2.setReservationStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        List<Reservation> retrievedReservations = reservationRepository.findAllByClientAndReservationStatus(client, ReservationStatus.CONFIRMED);


        assertEquals(1, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation2));
    }

    @Test
    public void testFindByReservationDate() {
        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 = createReservation(client, Shift.NIGHT2);
        Reservation reservation2 = createReservation(client, Shift.DAY2);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);


        List<Reservation> retrievedReservations = reservationRepository.findAllByReservationDate(LocalDate.of(2024, 05, 11));

;
        assertEquals(2, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation1));
        assertTrue(retrievedReservations.contains(reservation2));
    }

    @Test
    public void testFindByReservationDateAndShift() {
        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 = createReservation(client, Shift.DAY1);
        Reservation reservation2 = createReservation(client, Shift.NIGHT1);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        List<Reservation> retrievedReservations =
                reservationRepository.findAllByReservationDateAndShift(LocalDate.of(2024, 05, 11), Shift.DAY1);

        assertEquals(1, retrievedReservations.size());
        assertTrue(retrievedReservations.contains(reservation1));
    }

    @Test
    public void testFindByReservationDateAndShiftAndReservationStatus() {
        Client client = new Client();
        userRepository.save(client);

        Reservation reservation1 =  createReservation(client, Shift.DAY1);
        reservation1.setReservationStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation1);

        Reservation reservation2 = createReservation(client, Shift.DAY2);
        reservation2.setReservationDate(LocalDate.now());
        reservation2.setReservationStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation2);

        LocalDate date = LocalDate.now();
        Shift shift = Shift.DAY2;
        ReservationStatus status = ReservationStatus.CONFIRMED;
        List<Reservation> reservations = reservationRepository.findAllByReservationDateAndShiftAndReservationStatus(date, shift, status);


        assertEquals(1, reservations.size());
        assertNotEquals(reservation1.getReservationStatus(), reservations.get(0).getReservationStatus());
        assertNotEquals(reservation1.getShift(), reservations.get(0).getShift());
    }

    private Reservation createReservation(Client client, Shift shift) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDinners(2);
        reservation.setReservationDate(LocalDate.of(2024, 05, 11));
        reservation.setShift(shift);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservation;
    }

}