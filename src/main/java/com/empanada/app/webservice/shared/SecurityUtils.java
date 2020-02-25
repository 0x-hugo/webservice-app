package com.empanada.app.webservice.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.empanada.app.webservice.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class SecurityUtils {

  private final Random random = new SecureRandom();
  private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  public static final int DEFAULT_LENGTH = 30;

  public String generateUserId(int length) {
    return generateRandomString(length);
  }

  public String generateAddressId(int length) {
    return generateRandomString(length);
  }

  private String generateRandomString(int length) {
    final StringBuilder randomString = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      randomString.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    }

    return new String(randomString);
  }

  public static boolean hasTokenExpired(String token) {
    final Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
        .getBody();
    final Date tokenExpirationDate = claims.getExpiration();
    final Date todayDate = new Date();

    return tokenExpirationDate.before(todayDate);
  }

  public static String generateVerificationToken(String userId) {
    return Jwts.builder().setSubject(userId)
        .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
        .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();
  }

}
