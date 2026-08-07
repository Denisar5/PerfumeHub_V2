package com.denisar5.perfumehub.security;

import com.denisar5.perfumehub.controller.AdminController;
import com.denisar5.perfumehub.controller.ProfileController;
import com.denisar5.perfumehub.controller.ReviewController;
import com.denisar5.perfumehub.dto.request.AdminDashboardDto;
import com.denisar5.perfumehub.dto.response.UserViewDto;
import com.denisar5.perfumehub.enums.UserRole;
import com.denisar5.perfumehub.service.AdminService;
import com.denisar5.perfumehub.service.PerfumeService;
import com.denisar5.perfumehub.service.ReviewService;
import com.denisar5.perfumehub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        AdminController.class,
        ProfileController.class,
        ReviewController.class
})
@Import(SecurityConfiguration.class)
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private PerfumeService perfumeService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;




    @Test
    void guestShouldNotAccessAdminDashboard() throws Exception {

        mockMvc.perform(
                        get("/admin")
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrlPattern("**/login")
                );
    }

    @Test
    @WithMockUser(
            username = "denis",
            roles = "USER"
    )
    void normalUserShouldNotAccessAdminDashboard()
            throws Exception {

        mockMvc.perform(
                        get("/admin")
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "admin",
            roles = "ADMIN"
    )
    void adminShouldAccessAdminDashboard()
            throws Exception {

        AdminDashboardDto dashboard =
                AdminDashboardDto.builder()
                        .totalUsers(3)
                        .totalPerfumes(10)
                        .totalOrders(2)
                        .pendingReviews(1)
                        .latestOrders(List.of())
                        .latestReviews(List.of())
                        .build();

        when(adminService.getDashboardData())
                .thenReturn(dashboard);

        mockMvc.perform(
                        get("/admin")
                )
                .andExpect(
                        status().isOk()
                );
    }




    @Test
    void guestShouldNotAccessProfile() throws Exception {

        mockMvc.perform(
                        get("/profile")
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrlPattern("**/login")
                );
    }

    @Test
    @WithMockUser(
            username = "denis",
            roles = "USER"
    )
    void authenticatedUserShouldAccessProfile()
            throws Exception {

        UserViewDto user =
                UserViewDto.builder()
                        .id(UUID.randomUUID())
                        .username("denis")
                        .email("denis@test.com")
                        .firstName("Denis")
                        .lastName("Arnaudov")
                        .role(UserRole.USER)
                        .enabled(true)
                        .build();

        when(userService.getUserProfile("denis"))
                .thenReturn(user);

        mockMvc.perform(
                        get("/profile")
                )
                .andExpect(
                        status().isOk()
                );
    }




    @Test
    void guestShouldNotAccessMyReviews() throws Exception {

        mockMvc.perform(
                        get("/reviews/my")
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrlPattern("**/login")
                );
    }

    @Test
    @WithMockUser(
            username = "denis",
            roles = "USER"
    )
    void authenticatedUserShouldAccessMyReviews()
            throws Exception {

        when(reviewService.getReviewsForUser("denis"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/reviews/my")
                )
                .andExpect(
                        status().isOk()
                );
    }
}