package com.sophie.task_tracker.services;

import com.sophie.task_tracker.dto.AuthResponseDto;
import com.sophie.task_tracker.dto.LoginDto;
import com.sophie.task_tracker.dto.UserDto;
import com.sophie.task_tracker.dto.UserRegistrationDto;
import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthResponseDto register(UserRegistrationDto registrationDto) {
        UserDto userDto = userService.registerUser(registrationDto);
        
        User user = userService.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));
        
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        
        return new AuthResponseDto(token, "User registered successfully", userDto);
    }

    public AuthResponseDto login(LoginDto loginDto) {
        User user = userService.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!userService.verifyPassword(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDetails userDetails = loadUserByUsername(loginDto.getEmail());
        String token = jwtService.generateToken(userDetails);

        UserDto userDto = userMapper.toDto(user);

        return new AuthResponseDto(token, "Login successful", userDto);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
