package com.referral.service;

import com.referral.dto.ProfileUpdateRequest;
import com.referral.dto.SignupRequest;
import com.referral.model.User;
import com.referral.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_WithValidData_ShouldCreateUser() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.signup(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertNotNull(result.getReferralCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_WithExistingEmail_ShouldThrowException() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("existing@example.com");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.signup(request));
    }

    @Test
    void completeProfile_WithValidData_ShouldUpdateUser() {
        // Arrange
        String userId = "123";
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setPhoneNumber("1234567890");
        request.setAddress("123 Test St");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.completeProfile(userId, request);

        // Assert
        assertTrue(result.isProfileCompleted());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("123 Test St", result.getAddress());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getSuccessfulReferrals_ShouldReturnCompletedProfiles() {
        // Arrange
        String userId = "123";
        User referredUser1 = new User();
        referredUser1.setId("456");
        referredUser1.setProfileCompleted(true);

        User referredUser2 = new User();
        referredUser2.setId("789");
        referredUser2.setProfileCompleted(true);

        when(userRepository.findByReferredByAndProfileCompletedTrue(userId))
                .thenReturn(Arrays.asList(referredUser1, referredUser2));

        // Act
        List<User> result = userService.getSuccessfulReferrals(userId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(User::isProfileCompleted));
    }

    @Test
    void getReferralReport_ShouldCalculateCorrectStats() {
        // Arrange
        Integer userNumber = 1;
        User user = new User();
        user.setUserNumber(userNumber);
        user.setReferrals(Arrays.asList("ref1", "ref2", "ref3"));

        User completedRef1 = new User();
        completedRef1.setId("ref1");
        completedRef1.setProfileCompleted(true);

        User completedRef2 = new User();
        completedRef2.setId("ref2");
        completedRef2.setProfileCompleted(true);

        User pendingRef = new User();
        pendingRef.setId("ref3");
        pendingRef.setProfileCompleted(false);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userRepository.findById("ref1")).thenReturn(Optional.of(completedRef1));
        when(userRepository.findById("ref2")).thenReturn(Optional.of(completedRef2));
        when(userRepository.findById("ref3")).thenReturn(Optional.of(pendingRef));

        // Act
        UserService.ReferralReport report = userService.getReferralReportByUserNumber(userNumber);

        // Assert
        assertEquals(3, report.getTotalReferrals());
        assertEquals(2, report.getSuccessfulReferrals());
        assertEquals(1, report.getPendingReferrals());
    }
}