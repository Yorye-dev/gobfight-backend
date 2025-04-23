package dev.yorye.gobfight_backend.auth.controller;

import dev.yorye.gobfight_backend.auth.dto.LoginRequest;
import dev.yorye.gobfight_backend.auth.dto.RegisterRequest;
import dev.yorye.gobfight_backend.auth.dto.TokenResponse;
import dev.yorye.gobfight_backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/regiter")
    public ResponseEntity<TokenResponse> register(@RequestBody final RegisterRequest request) {

        TokenResponse token = authService.register(request);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginRequest request) {

        TokenResponse token = authService.login(request);

        return ResponseEntity.ok(token);
    }

    //@PostMapping("/reset/password") // No se si es necesario, ya que el usuario puede cambiar su contraseña desde el frontal

    //@PostMapping("/logout") Creo que no es necesario porque si se borra el Token desde el forntal la sesion se pierde

    //@PostMapping("/refresh/token")

}
