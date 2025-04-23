package dev.yorye.gobfight_backend.filter;

import dev.yorye.gobfight_backend.auth.service.JwtServiceImpl;
import dev.yorye.gobfight_backend.exception.InvalidTokenException;
import dev.yorye.gobfight_backend.exception.TokenExpiredException;
import dev.yorye.gobfight_backend.user.dto.UserDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtServiceImpl jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader  = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }
        final String token = authHeader.substring(7);

        try {
            if (jwtService.isTokenExpired(token)) {
                throw new TokenExpiredException("El token ha expirado");
            }
            // NO ME CUADRA ESTO, TENDRÏA QUE VERIFICAR SI EL TOKEN ES VALIDO
            if (!jwtService.isValidateToken(token, userDto)) {
                throw new InvalidTokenException("Token inválido");
            }

            UserDto userDto = jwtService.getUserDtoFromToken(token);

           //NO ME CUADRA ESTO
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDto, null, userDto.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
