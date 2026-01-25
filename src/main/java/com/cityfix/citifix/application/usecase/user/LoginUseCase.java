package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.LoginInputPort;
import com.cityfix.citifix.application.port.in.command.LoginCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase implements LoginInputPort {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepository;
    private final JwtService jwtService;

    @Override
    public String execute(LoginCommand command) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        command.email(),
                        command.password()
                )
        );

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        var springUser = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );

        return jwtService.generateToken(springUser);
    }
}