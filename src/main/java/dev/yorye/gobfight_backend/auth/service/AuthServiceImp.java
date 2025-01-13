package dev.yorye.gobfight_backend.auth.service;

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

        String hashedPassword = passwordEncoder.encode(request.password());
        UserDto userDto = UserMapper.toUserDto(request,hashedPassword);

        userService.createNewUser(userDto); // Persisntecia del suario en base de datos

        var jwtToken = jwtService.generateToken(userDto);

        return new TokenResponse(jwtToken, jwtToken);
        //TODO
       // Es necesario el persistir el token?.
    }
}
