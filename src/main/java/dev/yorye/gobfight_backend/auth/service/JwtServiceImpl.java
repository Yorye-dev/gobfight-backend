package dev.yorye.gobfight_backend.auth.service;

import dev.yorye.gobfight_backend.auth.constants.TokenType;
import dev.yorye.gobfight_backend.auth.dto.TokenDto;
import dev.yorye.gobfight_backend.auth.entity.Token;
import dev.yorye.gobfight_backend.auth.mapper.TokenMapper;
import dev.yorye.gobfight_backend.auth.repository.TokenRepository;
import dev.yorye.gobfight_backend.user.dto.UserDto;
import dev.yorye.gobfight_backend.user.entity.User;
import dev.yorye.gobfight_backend.user.mapper.UserMapper;
import dev.yorye.gobfight_backend.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Transactional
    @Override
    public String generateToken(final UserDto userDto) {

        String tokenString = buildToken(userDto);
        persistToken(tokenString, userDto.id(), TokenType.ACCESS);
        return tokenString;
    }

    @Override
    public boolean isValidateToken(final String token, UserDto user) {

        String extractedNicknameFromToken = extractNicknameFromToken(token);
        return extractedNicknameFromToken.equals(user.nickname());
    }

    @Transactional
    @Override
    public String generateRefreshToken(UserDto userDto) {
        String refreshToken = buildToken(userDto); // Puedes ajustar la expiración aquí
        persistToken(refreshToken, userDto.id(), TokenType.REFRESH);
        return refreshToken;
    }

    @Override
    public String generateRefreshToken(final String token) {
        // Implementar lógica para generar un nuevo token
        return "";
    }

    @Override
    public UserDto getUserDtoFromToken(final String token) {

        String nickname = extractNicknameFromToken(token);

        return UserDto.builder()
                .nickname(nickname)
                .build();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return false;
    }

    @Override
    public void deleteToken(String token) {

    }

    private String buildToken(UserDto userDto) {
        return Jwts.builder()
                .setSubject(userDto.nickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    private String extractTokenFromHeader(final String header) {
        // Implementar lógica para extraer el token del header
        return "";
    }

    private String extractNicknameFromToken(final String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("nickname", String.class);
        } catch (Exception e) {

            throw new RuntimeException("Error al procesar el token: " + e.getMessage());
        }
    }

    private SecretKey getSecretKey() {
        byte[] secretBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private void persistToken(String token, Long userId, TokenType type) {

        var tokenEntity = TokenMapper.toToken(TokenDto.builder()
                .token(token)
                .userId(userId)
                .type(type.name())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(expiration / 60000))
                .revoked(false)
                .build());

        tokenRepository.save(tokenEntity);
    }

}
