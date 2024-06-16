package com.esmartdie.EsmartCafeteriaApi.utils;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOConverterTest {

    @Test
    public void testCreateClientFromNewClientDTO() {

        DTOConverter dtoConverter = new DTOConverter();
        NewClientDTO newClientDTO = new NewClientDTO();
        newClientDTO.setName("John");
        newClientDTO.setLastName("Doe");
        newClientDTO.setEmail("john.doe@example.com");
        newClientDTO.setPassword("password123");
        newClientDTO.setActive(true);

        Role userRole = new Role(1L, "USER");


        Client client = dtoConverter.createClientFromNewClientDTO(newClientDTO, userRole);

        assertNotNull(client);
        assertNull(client.getId());
        assertEquals("John", client.getName());
        assertEquals("Doe", client.getLastName());
        assertEquals("john.doe@example.com", client.getEmail());
        assertEquals("password123", client.getPassword());
        assertTrue(client.getActive());
        assertEquals(userRole, client.getRole());
        assertEquals(5.0, client.getRating());
    }



    @Test
    public void testCreateClientDTOFromClient() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        Role userRole = new Role(1L, "USER");
        Client client = new Client(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                true,
                userRole
        );
        client.setRating(7.5);

        // Act
        ClientDTO clientDTO = dtoConverter.createClientDTOFromClient(client);

        // Assert
        assertNotNull(clientDTO);
        assertEquals(1L, clientDTO.getId());
        assertEquals("John", clientDTO.getName());
        assertEquals("Doe", clientDTO.getLastName());
        assertEquals("john.doe@example.com", clientDTO.getEmail());
        assertEquals(true, clientDTO.isActive());
        assertEquals(5.0, clientDTO.getRating());
    }

    @Test
    public void testCreateEmployeeFromEmployeeDTO() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName("Jane");
        employeeDTO.setLastName("Doe");
        employeeDTO.setEmail("jane.doe@example.com");
        employeeDTO.setPassword("password123");
        employeeDTO.setActive(true);
        employeeDTO.setEmployee_id(12345L);

        Role userRole = new Role(1L, "EMPLOYEE");

        // Act
        Employee employee = dtoConverter.createEmployeeFromEmployeeDTO(employeeDTO, userRole);

        // Assert
        assertNotNull(employee);
        assertNull(employee.getId());
        assertEquals("Jane", employee.getName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("jane.doe@example.com", employee.getEmail());
        assertEquals("password123", employee.getPassword());
        assertTrue(employee.getActive());
        assertEquals(userRole, employee.getRole());
        assertEquals(12345L, employee.getEmployee_id());
    }

    @Test
    public void testCreateEmployeeResponseDTOFromEmployee() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        Role userRole = new Role(1L, "EMPLOYEE");
        Employee employee = new Employee(
                1L,
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "password123",
                true,
                userRole,
                12345L
        );

        // Act
        EmployeeResponseDTO employeeResponseDTO = dtoConverter.createEmployeeResponseDTOFromEmployee(employee);

        // Assert
        assertNotNull(employeeResponseDTO);
        assertEquals(1L, employeeResponseDTO.getId());
        assertEquals("Jane", employeeResponseDTO.getName());
        assertEquals("Doe", employeeResponseDTO.getLastName());
        assertEquals("jane.doe@example.com", employeeResponseDTO.getEmail());
        assertTrue(employeeResponseDTO.isActive());
        assertEquals(12345L, employeeResponseDTO.getEmployee_id());
    }

    @Test
    public void testCreateClientDTOList() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        Role userRole = new Role(1L, "CLIENT");

        Client client1 = new Client(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                true,
                userRole
        );
        client1.setRating(7.5);

        Client client2 = new Client(
                2L,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "password456",
                true,
                userRole
        );
        client2.setRating(8.5);

        List<Client> clients = new ArrayList<>();
        clients.add(client1);
        clients.add(client2);

        // Act
        List<ClientDTO> clientDTOList = dtoConverter.createClientDTOList(clients);

        // Assert
        assertNotNull(clientDTOList);
        assertEquals(2, clientDTOList.size());

        ClientDTO clientDTO1 = clientDTOList.get(0);
        assertEquals(1L, clientDTO1.getId());
        assertEquals("John", clientDTO1.getName());
        assertEquals("Doe", clientDTO1.getLastName());
        assertEquals("john.doe@example.com", clientDTO1.getEmail());
        assertTrue(clientDTO1.isActive());
        assertEquals(5.0, clientDTO1.getRating());  // Default rating set by DTO

        ClientDTO clientDTO2 = clientDTOList.get(1);
        assertEquals(2L, clientDTO2.getId());
        assertEquals("Jane", clientDTO2.getName());
        assertEquals("Smith", clientDTO2.getLastName());
        assertEquals("jane.smith@example.com", clientDTO2.getEmail());
        assertTrue(clientDTO2.isActive());
        assertEquals(5.0, clientDTO2.getRating());  // Default rating set by DTO
    }

    @Test
    public void testCreateEmployeeResponseDTOList() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        Role userRole = new Role(1L, "EMPLOYEE");

        Employee employee1 = new Employee(
                1L,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                "password123",
                true,
                userRole,
                1001L
        );

        Employee employee2 = new Employee(
                2L,
                "Bob",
                "Green",
                "bob.green@example.com",
                "password456",
                true,
                userRole,
                1002L
        );

        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);

        // Act
        List<EmployeeResponseDTO> employeeDTOList = dtoConverter.createEmployeeResponseDTOList(employees);

        // Assert
        assertNotNull(employeeDTOList);
        assertEquals(2, employeeDTOList.size());

        EmployeeResponseDTO employeeDTO1 = employeeDTOList.get(0);
        assertEquals(1L, employeeDTO1.getId());
        assertEquals("Alice", employeeDTO1.getName());
        assertEquals("Brown", employeeDTO1.getLastName());
        assertEquals("alice.brown@example.com", employeeDTO1.getEmail());
        assertTrue(employeeDTO1.isActive());
        assertEquals(1001L, employeeDTO1.getEmployee_id());

        EmployeeResponseDTO employeeDTO2 = employeeDTOList.get(1);
        assertEquals(2L, employeeDTO2.getId());
        assertEquals("Bob", employeeDTO2.getName());
        assertEquals("Green", employeeDTO2.getLastName());
        assertEquals("bob.green@example.com", employeeDTO2.getEmail());
        assertTrue(employeeDTO2.isActive());
        assertEquals(1002L, employeeDTO2.getEmployee_id());
    }

    @Test
    public void testCreateReservationDTOFromReservation() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        Role userRole = new Role(1L, "CLIENT");
        Client client = new Client(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                true,
                userRole
        );

        ReservationRecord reservationRecord = new ReservationRecord();

        Reservation reservation = new Reservation(
                client,
                4,
                reservationRecord,
                LocalDate.now().plusDays(1),
                Shift.DAY1);
        reservation.setId(1L);
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);

        // Act
        ReservationDTO reservationDTO = dtoConverter.createReservationDTOFromReservation(reservation);

        // Assert
        assertNotNull(reservationDTO);
        assertEquals(1L, reservationDTO.getId());
        assertEquals(4, reservationDTO.getDinners());
        assertEquals(LocalDate.now().plusDays(1), reservationDTO.getReservationDate());
        assertEquals(Shift.DAY1, reservationDTO.getShift());
        assertEquals(ReservationStatus.CONFIRMED, reservationDTO.getReservationStatus());

        ClientDTO clientDTO = reservationDTO.getClientDTO();
        assertNotNull(clientDTO);
        assertEquals(1L, clientDTO.getId());
        assertEquals("John", clientDTO.getName());
        assertEquals("Doe", clientDTO.getLastName());
        assertEquals("john.doe@example.com", clientDTO.getEmail());
        assertTrue(clientDTO.isActive());
    }

    @Test
    public void testCreateReservationRecordDTOFromReservationRecord() {
        // Arrange
        DTOConverter dtoConverter = new DTOConverter();

        ReservationRecord reservationRecord = new ReservationRecord(
                LocalDate.now().plusDays(1),
                Shift.DAY1
        );
        reservationRecord.setId(1L);
        reservationRecord.setEmptySpaces(25);

        // Act
        ReservationRecordDTO reservationRecordDTO = dtoConverter.createReservationRecordDTOFromReservationRecord(reservationRecord);

        // Assert
        assertNotNull(reservationRecordDTO);
        assertEquals(1L, reservationRecordDTO.getId());
        assertEquals(LocalDate.now().plusDays(1), reservationRecordDTO.getReservationDate());
        assertEquals(Shift.DAY1, reservationRecordDTO.getShift());
        assertEquals(25, reservationRecordDTO.getAvailableReservations());
    }
}