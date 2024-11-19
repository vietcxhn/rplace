package com.projet.gestioncv.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ce filtre a la charge de récupérer le JWT dans les en-tetes, et de le
 * vérifier afin de construire le contexte de sécurité Spring Security.
 */
public class JwtFilter extends OncePerRequestFilter {

	private JwtProvider jwtTokenProvider;

	public JwtFilter(JwtProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
		System.out.println("Init JWT filter");
	}

	public String extractToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		String token = extractToken(httpServletRequest);

		try {
			if (token != null && jwtTokenProvider.validateToken(token)) {
				Authentication auth = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (JwtException ex) {
			// Cette ligne est très importante pour garantir que
			// le contexte de sécurité est bien supprimé.
			SecurityContextHolder.clearContext();
			httpServletResponse.sendError(ex.getStatusCode().value(), ex.getMessage());
			return;
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

}
