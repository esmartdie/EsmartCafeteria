package com.esmartdie.EsmartCafeteriaApi.security;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IUserLogsRepository userLogsRepository;

    private Client client;
    private Role role;

    @BeforeEach
    public void setup() {

        userLogsRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        role = new Role(null, "ROLE_USER");
        roleRepository.save(role);
        client = new Client(null, "John", "Doe", "JohnDoe@qa.com",  passwordEncoder.encode("password"), true, role);
        userRepository.save(client);
    }

    @AfterEach
    void tearDown(){
        userLogsRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/api/login")
                        .param("email", "JohnDoe@qa.com")
                        .param("password", "password"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        mockMvc.perform(post("/api/login")
                        .param("email", "JohnDoe@qa.com")
                        .param("password", "wrongpassword"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}