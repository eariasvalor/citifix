package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.command.LoginCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("Should return JWT token when credentials are valid")
    void shouldReturnTokenWhenValid() {
        String email = "alex@cityfix.com";
        String password = "securePass";
        LoginCommand command = new LoginCommand(email, password);
        User domainUser = User.create(email, "hashedPass", Set.of("ROLE_USER"));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(domainUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        String token = loginUseCase.execute(command);

        assertThat(token).isEqualTo("fake-jwt-token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception if user not found after auth (Edge Case)")
    void shouldThrowIfUserNotFound() {
        LoginCommand command = new LoginCommand("ghost@cityfix.com", "pass");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(command.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}