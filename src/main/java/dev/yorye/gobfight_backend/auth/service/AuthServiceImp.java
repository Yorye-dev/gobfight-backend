package dev.yorye.gobfight_backend.auth.service;

import dev.yorye.gobfight_backend.auth.dto.LoginRequest;
import dev.yorye.gobfight_backend.auth.dto.RegisterRequest;
import dev.yorye.gobfight_backend.auth.dto.TokenResponse;
import dev.yorye.gobfight_backend.user.dto.UserDto;
import dev.yorye.gobfight_backend.user.mapper.UserMapper;
import dev.yorye.gobfight_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public TokenResponse register(RegisterRequest request) {

        // TODO: Agregar validacion de usaurio y manejo de excepciones.

        String hashedPassword = passwordEncoder.encode(request.password());
        UserDto userDto = UserMapper.toUserDto(request,hashedPassword);

        ///  antes de creaer el token verificar si el usuario ya existe o el nickname ya esta en uso o el email esta en uso


        userDto = userService.createNewUser(userDto);


        var jwtToken = jwtService.generateToken(userDto);
        var refreshToken = jwtService.generateRefreshToken(userDto);

        return new TokenResponse(jwtToken, refreshToken);
    }

    @Override
    public TokenResponse login(LoginRequest request) {

        final UserDto userDto = userService.validateUser(request.nickname(), request.password());

        var jwtToken = jwtService.generateToken(userDto);

        var refreshToken = jwtService.generateRefreshToken(userDto);

        return new TokenResponse(jwtToken, refreshToken);
    }

    private boolean isPasswordCorrect(String password, String hashedPassword){
        return passwordEncoder.matches(password, hashedPassword);
    }
}
