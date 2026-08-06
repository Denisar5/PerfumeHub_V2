package com.denisar5.perfumehub.service.impl;

import com.denisar5.perfumehub.dto.request.ProfileEditDto;
import com.denisar5.perfumehub.dto.request.RegisterDto;
import com.denisar5.perfumehub.dto.request.RoleUpdateDto;
import com.denisar5.perfumehub.dto.response.UserViewDto;
import com.denisar5.perfumehub.entity.UserEntity;
import com.denisar5.perfumehub.enums.UserRole;
import com.denisar5.perfumehub.exception.DuplicateResourceException;
import com.denisar5.perfumehub.exception.InvalidOperationException;
import com.denisar5.perfumehub.exception.ResourceNotFoundException;
import com.denisar5.perfumehub.repository.UserRepository;
import com.denisar5.perfumehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterDto dto) {
        validateRegistration(dto);

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        log.info("Registered new user with username={}", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public UserViewDto getUserProfile(String username) {
        return mapToViewDto(findByUsername(username));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileEditDto getProfileEditDto(String username) {
        UserEntity user = findByUsername(username);

        ProfileEditDto dto = new ProfileEditDto();

        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        return dto;
    }

    @Override
    @Transactional
    public void updateProfile(
            String username,
            ProfileEditDto dto
    ) {
        UserEntity user = findByUsername(username);

        userRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .filter(foundUser -> !foundUser.getId().equals(user.getId()))
                .ifPresent(foundUser -> {
                    throw new DuplicateResourceException(
                            "Another user already uses this email"
                    );
                });

        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setPhoneNumber(normalizeNullable(dto.getPhoneNumber()));
        user.setAddress(normalizeNullable(dto.getAddress()));

        log.info("Updated profile for username={}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserViewDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateUserRole(
            UUID userId,
            RoleUpdateDto dto,
            String actingAdminUsername
    ) {
        UserEntity targetUser = findById(userId);
        UserEntity actingAdmin = findByUsername(actingAdminUsername);

        if (targetUser.getId().equals(actingAdmin.getId())) {
            throw new InvalidOperationException(
                    "Administrators cannot change their own role"
            );
        }

        targetUser.setRole(dto.getRole());

        log.info(
                "Admin username={} changed role for username={} to {}",
                actingAdminUsername,
                targetUser.getUsername(),
                dto.getRole()
        );
    }

    @Override
    @Transactional
    public void changeUserEnabledStatus(
            UUID userId,
            String actingAdminUsername
    ) {
        UserEntity targetUser = findById(userId);
        UserEntity actingAdmin = findByUsername(actingAdminUsername);

        if (targetUser.getId().equals(actingAdmin.getId())) {
            throw new InvalidOperationException(
                    "Administrators cannot disable their own account"
            );
        }

        targetUser.setEnabled(!targetUser.isEnabled());

        log.info(
                "Admin username={} changed enabled status for username={} to {}",
                actingAdminUsername,
                targetUser.getUsername(),
                targetUser.isEnabled()
        );
    }

    private void validateRegistration(RegisterDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidOperationException(
                    "Password and confirmation password do not match"
            );
        }

        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(
                dto.getEmail().trim().toLowerCase()
        )) {
            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }
    }

    private UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    private UserEntity findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " was not found"
                        )
                );
    }

    private UserViewDto mapToViewDto(UserEntity user) {
        return UserViewDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}