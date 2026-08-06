package com.denisar5.perfumehub.service;

import com.denisar5.perfumehub.dto.request.ProfileEditDto;
import com.denisar5.perfumehub.dto.request.RegisterDto;
import com.denisar5.perfumehub.dto.request.RoleUpdateDto;
import com.denisar5.perfumehub.dto.response.UserViewDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    void register(RegisterDto registerDto);

    UserViewDto getUserProfile(String username);

    ProfileEditDto getProfileEditDto(String username);

    void updateProfile(
            String username,
            ProfileEditDto profileEditDto
    );

    List<UserViewDto> getAllUsers();

    void updateUserRole(
            UUID userId,
            RoleUpdateDto roleUpdateDto,
            String actingAdminUsername
    );

    void changeUserEnabledStatus(
            UUID userId,
            String actingAdminUsername
    );
}