package com.sider.tienda.configuracion;



import com.sider.tienda.servicio.DetallesUsuarioServicio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final DetallesUsuarioServicio detallesUsuarioServicio;

    public SecurityConfig(DetallesUsuarioServicio detallesUsuarioServicio) {
        this.detallesUsuarioServicio = detallesUsuarioServicio;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(detallesUsuarioServicio);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // URLs públicas: login, recursos estáticos y nuestra nueva página de tienda
                        .requestMatchers("/login", "/css/**", "/js/**", "/tienda", "/registro", "/").permitAll()
                        // Las rutas de admin ya están protegidas a nivel de método en ProductoControlador
                        // con @PreAuthorize("hasRole('ADMIN')").
                        // Para cualquier otra petición, el usuario debe estar autenticado.
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/tienda")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }
}
