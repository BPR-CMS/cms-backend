package com.backend.cms.service;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.model.User;
import com.backend.cms.repository.ConfigRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AdminInitializationServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private ConfigRepository configRepository;

    @InjectMocks
    private AdminInitializationService adminInitializationService;

    @Test
    public void testInitializeAdmin_Success() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        request.setFirstName("Lori");
        request.setLastName("Johnson");
        request.setEmail("lori.johnson@example.com");
        request.setPassword("La1@aaaaaaaaaaa");

        // Creating a mock User object
        User createdUser = new User();

        // Call the method being tested
        UserDTO result = adminInitializationService.initializeAdmin(request);

        assertEquals(request.getFirstName(), result.getFirstName());
        assertEquals(request.getLastName(), result.getLastName());
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(createdUser.getPassword(), result.getPassword());
    }

    @Test
    public void testInitializeAdmin_Failure_InvalidInput() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Test for empty fields
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPassword("");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }

    @Test
    public void testInitializeAdmin_Failure_InvalidName_Short() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Invalid name (too short)
        // MIN_NAME_LENGTH = 2;
        request.setFirstName("L");
        request.setLastName("Johnson");
        request.setEmail("lori.johnson@example.com");
        request.setPassword("valid@Password123");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }

    @Test
    public void testInitializeAdmin_Failure_InvalidName_Long() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Invalid name (too long)
        // MAX_NAME_LENGTH = 20;
        request.setFirstName("abcabcabcabcabcabcabc");
        request.setLastName("Johnson");
        request.setEmail("lori.johnson@example.com");
        request.setPassword("valid@Password123");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }

    @Test
    public void testInitializeAdmin_Failure_InvalidPassword_Short() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        request.setFirstName("Lori");
        request.setLastName("Johnson");
        request.setEmail("lori.johnson@example.com");

        // Invalid password (too short)
        // MIN_PASSWORD_LENGTH = 8;
        request.setPassword("d@Pa1");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }

    @Test
    public void testInitializeAdmin_Failure_InvalidPassword_Long() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        request.setFirstName("Lori");
        request.setLastName("Johnson");
        request.setEmail("lori.johnson@example.com");

        // Invalid password (too long)
        // MAX_PASSWORD_LENGTH = 16;
        request.setPassword("valid@Password12311111111111111");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }


    @Test
    public void testInitializeAdmin_Failure_InvalidEmail() {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        request.setFirstName("Alice");
        request.setLastName("Johnson");

        // Invalid email format
        request.setEmail("lori@gmail");
        request.setPassword("validpassword");

        assertThrows(ResponseStatusException.class, () -> {
            adminInitializationService.initializeAdmin(request);
        });
    }
}
