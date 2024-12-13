package com.projet.rplace.security;

import com.projet.rplace.model.XUser;
import com.projet.rplace.repository.XUserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

/**
 * Configuration de Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableMethodSecurity
public class JwtWebSecurityConfig {

	private final XUserRepository repo;

	private final JwtProvider jwtTokenProvider;

	@Autowired
	public JwtWebSecurityConfig(XUserRepository repo, JwtProvider jwtTokenProvider) {
		this.repo = repo;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	protected final Log logger = LogFactory.getLog(getClass());

	@PostConstruct
	public void init() {
		var encoder = passwordEncoder();
		var aa = new XUser(
				0L,
				"admin",
				encoder.encode("admin"),
				Set.of("ADMIN", "USER")
		);
		var viet = new XUser(
				0L,
				"viet",
				encoder.encode("admin"),
				Set.of("USER")
		);
		repo.save(aa);
		repo.save(viet);
		logger.debug("--- INIT SPRING SECURITY JWT");
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// Pas de vérification CSRF (cross site request forgery)
		http.csrf(config -> {
			config.disable();
		});

		// Spring security de doit pas gérer les sessions
		http.sessionManagement(config -> {
			config.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		// Déclaration des end-points
		http.authorizeHttpRequests(config -> {//
			config.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
			// Pour les autres
			config.anyRequest().permitAll();
		});

		// Mise en place du filtre JWT
		JwtFilter customFilter = new JwtFilter(jwtTokenProvider);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
//		http.apply(new JwtFilterConfigurer(jwtTokenProvider));

		return http.build();
	}

	/*
	 * Définir le fournisseur d'authentification. Nous utilisons la version
	 * DaoAuthenticationProvider qui récupère les informations à partir du
	 * UserDetailsService que nous avons défini avant.
	 */
	@Bean
	public AuthenticationProvider myAuthenticationProvider(//
			@Autowired PasswordEncoder encoder, //
			@Autowired UserDetailsService userDetailsService) {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder);
		return authProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

}
