package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IRoleRepositoryTest {

    @Autowired
    private IRoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_ADMIN");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_USER");
        roleRepository.save(role2);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void testFindByName_RoleFound() {
        String roleName = "ROLE_ADMIN";
        Role foundRole = roleRepository.findByName(roleName);

        assertNotNull(foundRole);
        assertEquals(roleName, foundRole.getName());
    }

    @Test
    void testFindByName_RoleNotFound() {
        String roleName = "ROLE_NON_EXISTING";
        Role foundRole = roleRepository.findByName(roleName);
        assertNull(foundRole);
    }
}