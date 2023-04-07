package com.project202223t2g1t1.transcenda.User;


import com.project202223t2g1t1.transcenda.Registration.Token.ConfirmationToken;
import com.project202223t2g1t1.transcenda.Registration.Token.ConfirmationTokenService;
import com.project202223t2g1t1.transcenda.Security.JwtService;
import com.project202223t2g1t1.transcenda.Exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ConfirmationTokenService confirmationTokenService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user) {
        //checks for duplicate and other verifications are done in the registration service

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    public User findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        try {
            User user = userRepository.findByEmail(email).get();
            user.setResetPasswordToken(token);
            userRepository.save(user);
        }
        catch (NoSuchElementException e) {
            throw new UserNotFoundException("Could not find any user with the email: " + email);
        }

    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public String authenticateUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
        return jwtService.generateToken(user); //generate and return the jwt token to the user
    }
}
