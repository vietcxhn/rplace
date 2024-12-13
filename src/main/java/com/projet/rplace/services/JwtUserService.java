package com.projet.rplace.services;

import com.projet.rplace.controller.AuthController;
import com.projet.rplace.model.XUser;
import com.projet.rplace.repository.XUserRepository;
import com.projet.rplace.security.JwtException;
import com.projet.rplace.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserService {

    @Autowired
    private XUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtTokenProvider;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    public String login(String username, String password) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            var user = userRepository.findByUsername(username).get();
            return jwtTokenProvider.createToken(user);
        } catch (AuthenticationException e) {
            throw new JwtException("Invalid username/password supplied");
        }
    }

    public String signup(XUser user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new JwtException("Username is already in use");
        }

        userRepository.save(user);
        return jwtTokenProvider.createToken(user);
    }

    public String refresh(String username) {
        return null;
    }

    public void removeToken(String token) {
        jwtTokenProvider.removeToken(token);
    }

    public void logout() {
    }

    public Optional<XUser> getUser(String name) {
        return userRepository.findByUsername(name);
    }
}
