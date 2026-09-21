package com.taskmanager.auth.service;

import com.taskmanager.auth.dto.AuthResponse;
import com.taskmanager.auth.dto.LoginRequest;
import com.taskmanager.auth.dto.RegisterRequest;
import com.taskmanager.auth.security.JwtService;
import com.taskmanager.user.dto.UserResponse;
import com.taskmanager.user.mapper.UserMapper;
import com.taskmanager.user.model.RefreshToken;
import com.taskmanager.user.model.User;
import com.taskmanager.user.repository.RefreshTokenRepository;
import com.taskmanager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;

    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, refreshTokenRepository, passwordEncoder, jwtService, userMapper);
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604_800_000L);

        existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("jane@example.com")
                .passwordHash("hashed-password")
                .fullName("Jane Doe")
                .build();
    }

    @Test
    void register_savesNewUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123", "New User");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(userMapper.toResponse(any(User.class)))
                .thenReturn(new UserResponse(UUID.randomUUID(), "new@example.com", "New User", null, null));

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("jane@example.com", "password123", "Jane Doe");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsTokensWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("jane@example.com", "correct-password");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correct-password", existingUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateAccessToken(existingUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(existingUser)).thenReturn("refresh-token");
        when(userMapper.toResponse(existingUser))
                .thenReturn(new UserResponse(existingUser.getId(), existingUser.getEmail(), existingUser.getFullName(), null, null));

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_throwsBadCredentialsWhenEmailNotFound() {
        LoginRequest request = new LoginRequest("unknown@example.com", "whatever");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_throwsBadCredentialsWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("jane@example.com", "wrong-password");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong-password", existingUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refreshToken_rotatesTokenAndRevokesOldOne() {
        RefreshToken stored = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .token("old-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("old-refresh-token")).thenReturn(Optional.of(stored));
        when(jwtService.generateAccessToken(existingUser)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(existingUser)).thenReturn("new-refresh-token");
        when(userMapper.toResponse(existingUser))
                .thenReturn(new UserResponse(existingUser.getId(), existingUser.getEmail(), existingUser.getFullName(), null, null));

        AuthResponse response = authService.refreshToken("old-refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");

        ArgumentCaptor<RefreshToken> savedCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(savedCaptor.capture());
        assertThat(savedCaptor.getAllValues().get(0).isRevoked()).isTrue();
    }

    @Test
    void refreshToken_throwsWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken("missing"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refreshToken_throwsWhenTokenIsRevoked() {
        RefreshToken stored = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .token("revoked-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(true)
                .build();
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refreshToken("revoked-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refreshToken_throwsWhenTokenIsExpired() {
        RefreshToken stored = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refreshToken("expired-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void logout_revokesStoredToken() {
        RefreshToken stored = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .token("some-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("some-token")).thenReturn(Optional.of(stored));

        authService.logout("some-token");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().isRevoked()).isTrue();
    }

    @Test
    void logout_doesNothingWhenTokenDoesNotExist() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        authService.logout("missing");

        verify(refreshTokenRepository, never()).save(any());
    }
}
