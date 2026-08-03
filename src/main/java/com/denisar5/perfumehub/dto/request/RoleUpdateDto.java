package com.denisar5.perfumehub.dto.request;

import com.denisar5.perfumehub.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateDto {

    @NotNull(message = "Role is required")
    private UserRole role;
}