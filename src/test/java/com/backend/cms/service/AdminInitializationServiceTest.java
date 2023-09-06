package com.backend.cms.service;

import com.backend.cms.dto.RegisterUserDTO;
import com.backend.cms.model.User;
import com.backend.cms.repository.ConfigRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        request.setPassword("La1@aaaaaaaaa");

        // Creating a mock User object
        User createdUser = new User();

        // Call the method being tested
        RegisterUserDTO result = adminInitializationService.initializeAdmin(request);

        // Verifying that the response is as expected
        assertEquals(request.getFirstName(), result.getFirstName());
        assertEquals(request.getLastName(), result.getLastName());
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(createdUser.getPassword(), result.getPassword());
    }
}
