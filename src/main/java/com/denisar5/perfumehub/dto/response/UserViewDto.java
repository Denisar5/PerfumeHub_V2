package com.denisar5.perfumehub.dto.response;

import com.denisar5.perfumehub.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserViewDto {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private UserRole role;

    private boolean enabled;
}