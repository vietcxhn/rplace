package com.projet.gestioncv.security;

import com.projet.gestioncv.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Création/vérification/gestion d'un JWT
 */
@Component
@Profile("usejwt")
public class JwtProvider {

	/**
	 * Par simplicité, nous stockons la clef de manière statique. il est sans doute
	 * préférable d'avoir un autre API (sur un serveur de configuration) qui nous
	 * fournisse la clé.
	 */
	@Value("${security.jwt.token.secret-key:my-very-big-secret-phrase-for-signature}")
	private String secretText;

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000; // 1h

	private SecretKey secretKey;

	private final List<String> tokens = new ArrayList<>();

	@Autowired
	private JwtUserDetails myUserDetails;

	@PostConstruct
	protected void init() {
		secretKey = Keys.hmacShaKeyFor(secretText.getBytes());
	}

	public String createToken(Person user) {

		var rolesAsString = user.getRoles().stream()//
				.filter(Objects::nonNull)//
				.collect(Collectors.toList());

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		System.out.println("create token for " + user.getUsername());
		System.out.println("date = " + now);
		System.out.println("date = " + validity);

		String token = Jwts.builder()//
				.subject(user.getUsername())//
				.claim("auth", rolesAsString)//
				.issuedAt(now)//
				.expiration(validity)//
				.signWith(secretKey).compact();

		tokens.add(token);
		return token;
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return Jwts.parser()//
				.verifyWith(secretKey).build()//
				.parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token) {
		System.out.println("Before validate token " + token);
		try {
			if (!tokens.contains(token)) {
				System.out.println("Token not valid");
				return false;
			}
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException("Expired or invalid JWT token");
		}
	}

	public void removeToken(String token) {
		tokens.remove(token);
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser().verifyWith(secretKey).build()
					.parseSignedClaims(token)
					.getPayload();

			Date expiration = claims.getExpiration();  // Extract the expiration date
			return expiration.before(new Date());      // Check if token has expired
		} catch (SignatureException | IllegalArgumentException e) {
			// Handle invalid or corrupted tokens
			return true; // If there's an error parsing the token, consider it expired
		}
	}

	@Scheduled(fixedRate = 1000)
	public void removeExpiredTokens() {
		Iterator<String> iterator = tokens.iterator();
		while (iterator.hasNext()) {
			String token = iterator.next();

			// Check if the token is expired using the JwtUtils class
			if (isTokenExpired(token)) {
				iterator.remove();  // Remove expired token from the list
				System.out.println("Removed expired token: " + token);
			}
		}
	}
}
