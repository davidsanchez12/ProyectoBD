package com.unah.ProyectoBD.Config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // <-- IMPORTANTE
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <-- IMPORTANTE
import org.springframework.security.crypto.password.PasswordEncoder; // <-- IMPORTANTE
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // <-- 1. Habilita la configuración de seguridad web de Spring
public class SecurityConfig {

    /**
     * 2. DEFINE EL BEAN DEL PASSWORDENCODER
     * Esto soluciona el error que tenías. Le dice a Spring cómo crear
     * el codificador de contraseñas que tu servicio necesita.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 3. CONFIGURA LAS REGLAS DE ACCESO Y EL LOGIN
     * Define qué páginas son públicas y cuáles requieren que el usuario inicie
     * sesión.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Rutas públicas que cualquiera puede ver (login, registro, archivos CSS/JS)
                        .requestMatchers("/", "/login", "/registro/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // (Opcional) Aquí puedes añadir reglas por rol más adelante, por ejemplo:
                        // .requestMatchers("/tutor/**").hasAuthority("TUTOR")
                        // .requestMatchers("/estudiante/**").hasAuthority("ESTUDIANTE")

                        // Cualquier otra ruta que no coincida con las anteriores requiere autenticación
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // Especifica la URL de tu página de login personalizada
                        .loginProcessingUrl("/login") // La URL a la que el formulario enviará los datos
                        .defaultSuccessUrl("/dashboard", true) // A dónde ir después de un login exitoso
                        .permitAll() // Permite a todos acceder a la página de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para activar el cierre de sesión
                        .logoutSuccessUrl("/login?logout") // A dónde ir después de cerrar sesión
                        .permitAll())
                .csrf(csrf -> csrf.disable()); // Se mantiene para simplificar durante el desarrollo

        return http.build();
    }

    // Este bean está bien aquí, no necesita cambios.
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}