package dev.yorye.gobfight_backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .csrf(csrf -> csrf.disable()) // Para desarrollo; habilitar en producción.
               .authorizeHttpRequests(auth -> auth
                               .requestMatchers("/auth/**").permitAll() // Rutas públicas.
                               .anyRequest().authenticated() // Todas las demás requieren autenticación.
               );
                //.httpBasic(httpBasic -> httpBasic.disable()) // Desactivamos Basic Auth si vamos a usar JWT.
               // .formLogin(formLogin -> formLogin.disable()); // No usamos formularios tradicionales de login.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usamos BCrypt para encriptar contraseñas.
    }


}
