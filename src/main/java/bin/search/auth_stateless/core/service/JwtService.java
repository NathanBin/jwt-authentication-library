package bin.search.auth_stateless.core.service;

import bin.search.auth_stateless.core.dto.AuthUserResponse;
import bin.search.auth_stateless.infra.exception.AuthenticationException;
import bin.search.auth_stateless.infra.exception.ValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.ObjectUtils;
import javax.crypto.SecretKey;

public class JwtService {
    private final String secretKey;

    public JwtService(String secretKey) {
        this.secretKey = secretKey;
    }

    public AuthUserResponse getAuthUser(String token) {
        Claims tokenClaims = getClaims(token);
        Integer userId = Integer.valueOf(tokenClaims.get("id").toString());
        return new AuthUserResponse(userId, tokenClaims.get("email").toString(), tokenClaims.get("type").toString());
    }

    public void validateAccessToken(String token) {
        getClaims(token);
    }

    private Claims getClaims(String token) {
        String accesToken = extractToken(token);
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(generateSign())
                    .build()
                    .parseClaimsJws(accesToken)
                    .getBody();
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token " + e.getMessage());
        }
    }

    private SecretKey generateSign(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private String extractToken(String token) {
        if (ObjectUtils.isEmpty(token)){
            throw new ValidationException("Token is required");
        }

        if (token.contains(" ")){
            return token.split(" ")[1];
        }

        return token;
    }
}
