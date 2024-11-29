package com.projet.gestioncv.service;

import com.projet.gestioncv.model.Person;
import com.projet.gestioncv.repository.PersonRepository;
import com.projet.gestioncv.security.JwtException;
import com.projet.gestioncv.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Service
@Profile("usejwt")
public class JwtUserService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtProvider jwtTokenProvider;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	public String login(String username, String password) {
		try {
			authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			var user = personRepository.findById(username).get();
			return jwtTokenProvider.createToken(user);
		} catch (AuthenticationException e) {
			throw new JwtException("Invalid username/password supplied");
		}
	}

	public String signup(Person person) {
		if (personRepository.existsById(person.getUsername())) {
			throw new JwtException("Username is already in use");
		}

		personRepository.save(person);
		return jwtTokenProvider.createToken(person);
	}

	public String refresh(String username) {
		return null;
	}

	public void removeToken(String token) {
		jwtTokenProvider.removeToken(token);
	}

	public void logout() {
	}
}
