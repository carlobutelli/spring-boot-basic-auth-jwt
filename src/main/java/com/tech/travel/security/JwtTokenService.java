package com.tech.travel.security;

import com.tech.travel.services.UserService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    @Value("${travel.app.jwtSecret}")
    private String jwtSecret;

    @Value("${travel.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateToken(UserService userDetails, List<String> roles, boolean access) {
        String tokenType = "access_token";
        if(!access) {
            jwtExpirationMs = 31540000000L;
            tokenType = "refresh_token";
        }

        Claims claims = Jwts.claims();
        claims.put("user_id", userDetails.getId());
        claims.put("grant_type", tokenType);
        claims.put("authority", roles);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject((userDetails.getUsername()))
                .claim("identity", claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
