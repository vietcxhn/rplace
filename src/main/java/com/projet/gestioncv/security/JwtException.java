package com.projet.gestioncv.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

/*
 * Une exception pour coder les problèmes d'authentification.
 */
public class JwtException extends HttpServerErrorException {

	private static final long serialVersionUID = 1L;

	public JwtException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

}
