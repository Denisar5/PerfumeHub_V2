package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.ProfileEditDto;
import com.denisar5.perfumehub.dto.request.RegisterDto;
import com.denisar5.perfumehub.dto.request.RoleUpdateDto;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.UserRole;
import com.denisar5.perfumehub.exception.DuplicateResourceException;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private UserEntity user;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .username("denis")
                .email("denis@test.com")
                .password("encoded")
                .firstName("Denis")
                .lastName("Arnaudov")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        admin = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .email("admin@test.com")
                .password("encoded")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void registerShouldSaveUser() {
        RegisterDto dto = new RegisterDto();

        dto.setUsername("newuser");
        dto.setEmail("new@test.com");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");

        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("new@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded-password");

        userService.register(dto);

        verify(userRepository)
                .save(any(UserEntity.class));
    }

    @Test
    void registerShouldFailWhenUsernameExists() {
        RegisterDto dto = new RegisterDto();

        dto.setUsername("denis");
        dto.setEmail("new@test.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");

        when(userRepository.existsByUsername("denis"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.register(dto)
        );

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void registerShouldFailWhenEmailExists() {
        RegisterDto dto = new RegisterDto();

        dto.setUsername("newuser");
        dto.setEmail("denis@test.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");

        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("denis@test.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.register(dto)
        );

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void getUserProfileShouldReturnUser() {
        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        var result =
                userService.getUserProfile("denis");

        assertNotNull(result);
        assertEquals("denis", result.getUsername());
        assertEquals("denis@test.com", result.getEmail());
        assertEquals("Denis", result.getFirstName());
        assertEquals("Arnaudov", result.getLastName());
        assertEquals(UserRole.USER, result.getRole());
        assertTrue(result.isEnabled());
    }

    @Test
    void getUserProfileShouldThrowWhenMissing() {
        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserProfile("missing")
        );
    }

    @Test
    void updateProfileShouldChangeFields() {
        when(userRepository.findByUsername("denis"))
                .thenReturn(Optional.of(user));

        ProfileEditDto dto = new ProfileEditDto();

        dto.setEmail("updated@test.com");
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setPhoneNumber("0888123456");
        dto.setAddress("Sofia");

        userService.updateProfile(
                "denis",
                dto
        );

        assertEquals(
                "updated@test.com",
                user.getEmail()
        );

        assertEquals(
                "Updated",
                user.getFirstName()
        );

        assertEquals(
                "Name",
                user.getLastName()
        );

        assertEquals(
                "0888123456",
                user.getPhoneNumber()
        );

        assertEquals(
                "Sofia",
                user.getAddress()
        );
    }

    @Test
    void updateUserRoleShouldChangeRole() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        RoleUpdateDto dto = new RoleUpdateDto();
        dto.setRole(UserRole.ADMIN);

        userService.updateUserRole(
                userId,
                dto,
                "admin"
        );

        assertEquals(
                UserRole.ADMIN,
                user.getRole()
        );
    }

    @Test
    void changeUserEnabledStatusShouldToggleEnabled() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertTrue(user.isEnabled());

        userService.changeUserEnabledStatus(
                userId,
                "admin"
        );

        assertFalse(user.isEnabled());
    }

    @Test
    void adminShouldNotDisableOwnAccount() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));

        assertThrows(
                InvalidOperationException.class,
                () -> userService.changeUserEnabledStatus(
                        admin.getId(),
                        "admin"
                )
        );

        assertTrue(admin.isEnabled());
    }
}