package com.esmartdie.EsmartCafeteriaApi;

import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.IReservationRecordService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DemoDataLoader {

    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IReservationRepository reservationRepository;
    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRecordService reservationRecordService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @PostConstruct
    public void loadDemoData() {
        createRoles();
        createUsers();
        openCalendar();
        createReservations();
    }

    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate tomorrow = today.plusDays(1);

    private void createRoles(){
        List<Role> roles = Arrays.asList(
                new Role(null, "ROLE_USER"),
                new Role(null, "ROLE_ADMIN"),
                new Role(null, "ROLE_MODERATOR")
        );
        roleRepository.saveAll(roles);

    }
    private void createUsers(){
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
        Role moderatorRole = roleRepository.findByName("ROLE_MODERATOR").get();

        List<User> users = Arrays.asList(
                new Client(null, "John", "Constantine", "johnconstatine@qa.com",passwordEncoder().encode("1234"), true, userRole),
                new Client(null, "Bruce", "Wayne", "brucewayne@qa.com", passwordEncoder().encode("1234"), true, userRole),
                new Employee(null, "Peter", "Parker", "spiderman@qa.com", passwordEncoder().encode("1234"), true, moderatorRole, 123L),
                new Employee(null, "Clark", "Kent", "superman@qa.com", passwordEncoder().encode("1234"), true, adminRole,234L)
        );
        userRepository.saveAll(users);

    }

    private void openCalendar(){
        reservationRecordService.createMonthCalendar(YearMonth.of(2024, 05));
    }

    private void createReservations(){

        Client client1 = (Client)userRepository.findByEmail("johnconstatine@qa.com").get();

        List<Reservation> reservationList1 = Arrays.asList(
                new Reservation (null, client1, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.DAY2).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client1, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.DAY4).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client1, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.NIGHT1).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client1, 4,
                        reservationRecordRepository.findByReservationDateAndShift(today,Shift.NIGHT1).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client1, 4,
                        reservationRecordRepository.findByReservationDateAndShift(tomorrow,Shift.NIGHT4).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED)
        );
        reservationRepository.saveAll(reservationList1);

        Client client2 = (Client)userRepository.findByEmail("brucewayne@qa.com").get();

        List<Reservation> reservationList2 = Arrays.asList(
                new Reservation (null, client2, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.DAY2).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client2, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.DAY4).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client2, 4,
                        reservationRecordRepository.findByReservationDateAndShift(yesterday,Shift.NIGHT1).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client2, 4,
                        reservationRecordRepository.findByReservationDateAndShift(today,Shift.NIGHT1).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED),
                new Reservation (null, client2, 4,
                        reservationRecordRepository.findByReservationDateAndShift(tomorrow,Shift.NIGHT4).get(),
                        yesterday,Shift.DAY2, ReservationStatus.ACCEPTED)
        );
        reservationRepository.saveAll(reservationList2);

    }

}