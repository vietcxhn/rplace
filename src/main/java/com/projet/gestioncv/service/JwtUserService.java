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

import java.util.Optional;

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

	public String signup(Person user) {
		if (personRepository.existsById(user.getUsername())) {
			throw new JwtException("Username is already in use");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		personRepository.save(user);
		return jwtTokenProvider.createToken(user);
	}

	public void delete(String username) {
		personRepository.deleteById(username);
	}

	public Optional<Person> search(String username) {
		return personRepository.findById(username);
	}

	public String refresh(String username) {
		return jwtTokenProvider.createToken(personRepository.findById(username).get());
	}

	public void removeToken(String token) {
		jwtTokenProvider.removeToken(token);
	}
}
