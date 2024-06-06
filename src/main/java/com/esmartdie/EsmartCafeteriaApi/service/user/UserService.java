package com.esmartdie.EsmartCafeteriaApi.service.user;


import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.exception.EmailAlreadyExistsException;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UserTypeMismatchException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final DTOConverter converter;

    @Override
    public <T extends User> T saveUser(T user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public ClientDTO createClientFromDTO(NewClientDTO clientDTO) {

        checkEmailAvailability(clientDTO.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        Client client = saveUser(converter.createClientFromNewClientDTO(clientDTO, userRole));

        return converter.createClientDTOFromClient(client);
    }

    @Override
    public EmployeeResponseDTO createEmployeeFromDTO(EmployeeDTO employeeDTO) {

        checkEmailAvailability(employeeDTO.getEmail());

        Role userRole = roleRepository.findByName("ROLE_MODERATOR")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_MODERATOR")));


        Employee employee = saveUser(converter.createEmployeeFromEmployeeDTO(employeeDTO, userRole));

        return converter.createEmployeeResponseDTOFromEmployee(employee);

    }

    private void checkEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("The email \" + email + \" is already registered");
            throw new EmailAlreadyExistsException("The email " + email + " is already registered");
        }
    }

    @Override
    public Client getClientById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!(user instanceof Client)) {
            throw new UserTypeMismatchException("User with id " + id + " is not a Client");
        }

        return (Client) user;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!(user instanceof Employee)) {
            throw new UserTypeMismatchException("User with id " + id + " is not a Employee");
        }

        return (Employee) user;
    }

    @Override
    public ClientDTO updateClientFromDTO(Long id, UpdateClientDTO clientDTO) {

        Client client = (Client) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        checkEmailAvailabilityFilterIdResult(clientDTO.getEmail(), id);

        client.setName(clientDTO.getName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());

        client=userRepository.save(client);

        return converter.createClientDTOFromClient(client);
    }

    private void checkEmailAvailabilityFilterIdResult(String email, Long id) {
        if (existsByEmailAndIdNot(email, id)) {
            log.error("The email \" + email + \" is already registered");
            throw new EmailAlreadyExistsException("The email " + email + " is already registered");
        }
    }

    private boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.findByEmail(email)
                .map(user -> !user.getId().equals(id))
                .orElse(false);
    }


}
