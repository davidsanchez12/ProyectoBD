package com.unah.ProyectoBD.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Muestra la página de login personalizada.
     * La ruta "/login" está configurada en tu SecurityConfig.
     */
    @GetMapping("/login")
    public String mostrarPaginaLogin() {
        return "login"; // Devuelve el nombre de tu archivo login.html
    }

    /**
     * Punto central de redirección después de un login exitoso.
     * SecurityConfig está configurado para redirigir aquí.
     * Este método revisa el rol del usuario y lo envía a su panel correspondiente.
     */
    @GetMapping("/dashboard")
    public String redirigirPorRol(Authentication authentication) {
        // Obtenemos la primera autoridad (rol) del usuario autenticado.
        String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        if ("ESTUDIANTE".equals(rol)) {
            return "redirect:/tutorias/disponibles"; // Panel del estudiante
        }

        if ("TUTOR".equals(rol)) {
            return "redirect:/tutorias/mis-tutorias"; // Panel del tutor
        }

        // Si no tiene un rol conocido, lo mandamos a la página de inicio o de error.
        return "redirect:/";
    }

}