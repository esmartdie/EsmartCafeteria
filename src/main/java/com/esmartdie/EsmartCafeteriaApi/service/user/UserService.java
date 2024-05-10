package com.esmartdie.EsmartCafeteriaApi.service.user;


import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.EmailAlreadyExistsException;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UserTypeMismatchException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;

import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        User user = userRepository.findByName(username);


        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);

            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());

            return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), Collections.singleton(authority));
        }
    }

    @Override
    public <T extends User> T saveUser(T user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Client createClientFromDTO(ClientDTO clientDTO) {

        checkEmailAvailability(clientDTO.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        return new Client(
                null,
                clientDTO.getName(),
                clientDTO.getLastName(),
                clientDTO.getEmail(),
                clientDTO.getPassword(),
                clientDTO.isActive(),
                userRole
        );
    }

    @Override
    public Employee createEmployeeFromDTO(EmployeeDTO employeeDTO) {

        checkEmailAvailability(employeeDTO.getEmail());

        Role userRole = roleRepository.findByName("ROLE_MODERATOR")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_MODERATOR")));

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

    public void checkEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("The email \" + email + \" is already registered");
            throw new EmailAlreadyExistsException("The email " + email + " is already registered");
        }
    }

    /*
    @Override
    public User getUserById(Long id) {
        log.info("Fetching user {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

     */

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
    public Client updateClientFromDTO(Long id, ClientDTO clientDTO) {

        Client client = (Client) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        checkEmailAvailabilityFilterIdResult(clientDTO.getEmail(), id);

        client.setName(clientDTO.getName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());

        return client;
    }

    public void checkEmailAvailabilityFilterIdResult(String email, Long id) {
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            log.error("The email \" + email + \" is already registered");
            throw new EmailAlreadyExistsException("The email " + email + " is already registered");
        }
    }
/*
    @Override
    public Optional<User> getUserByEmail(String email) {
        log.info("Fetching user {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

 */


}
