package com.empanada.app.webservice.shared.utils;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.empanada.app.webservice.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {
	
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final int RANDOM_LENGTH = 30;
	
	public String generateUserId(int length) {
		return generateRandomString(length);
	}
	
	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		
		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		
		return new String(returnValue);
	}

	public static boolean hasTokenExpired(String token) {
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody();
		Date tokenExpirationDate = claims.getExpiration();
		Date todayDate = new Date();
		
		return tokenExpirationDate.before(todayDate);
	}

	public static String generateVerificationToken(String userId) {
		String token = Jwts.builder()
						.setSubject(userId)
						.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
						.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
						.compact();
		
		return token;
	}

}
