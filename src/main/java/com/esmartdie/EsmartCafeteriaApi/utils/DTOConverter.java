package com.esmartdie.EsmartCafeteriaApi.utils;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DTOConverter {

    public Client convertClientFromNewClientDTO (NewClientDTO newClientDTO, Role userRole){
        return new Client(
                null,
                newClientDTO.getName(),
                newClientDTO.getLastName(),
                newClientDTO.getEmail(),
                newClientDTO.getPassword(),
                newClientDTO.isActive(),
                userRole
        );
    }

    public ClientDTO convertClientDTOFromClient (Client client){

        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getLastName(),
                client.getEmail(),
                client.getActive()
        );
    }

    public Employee convertEmployeeFromEmployeeDTO (EmployeeDTO employeeDTO, Role userRole){

        return new Employee(
                null,
                employeeDTO.getName(),
                employeeDTO.getLastName(),
                employeeDTO.getEmail(),
                employeeDTO.getPassword(),
                employeeDTO.isActive(),
                userRole,
                employeeDTO.getEmployee_id()
        );
    }

    public EmployeeResponseDTO convertEmployeeResponseDTOFromEmployee (Employee employee){

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getActive(),
                employee.getEmployee_id()
        );
    }

    public List<ClientDTO> createClientDTOList(List<Client> clients) {
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (Client client : clients) {
            ClientDTO clientDTO = convertClientDTOFromClient(client);
            clientDTOList.add(clientDTO);
        }
        return clientDTOList;
    }

    public List<EmployeeResponseDTO> createEmployeeResponseDTOList(List<Employee> employees) {
        List<EmployeeResponseDTO> employeeDTOList = new ArrayList<>();
        for (Employee employee : employees) {
            EmployeeResponseDTO employeeDTO = convertEmployeeResponseDTOFromEmployee(employee);
            employeeDTOList.add(employeeDTO);
        }
        return employeeDTOList;
    }

    public ReservationDTO convertReservationDTOFromReservation (Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDinners(reservation.getDinners());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setShift(reservation.getShift());
        dto.setReservationStatus(reservation.getReservationStatus());

        ClientDTO clientDTO = convertClientDTOFromClient(reservation.getClient());
        dto.setClientDTO(clientDTO);

        return dto;
    }

}
