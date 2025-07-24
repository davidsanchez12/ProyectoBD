/*
 * package com.unah.ProyectoBD.Controllers;
 * 
 * // Puedes crear un nuevo controlador, por ejemplo, AuthController.java
 * 
 * import org.springframework.security.core.Authentication;
 * import org.springframework.stereotype.Controller;
 * import org.springframework.web.bind.annotation.GetMapping;
 * import org.springframework.web.bind.annotation.PostMapping;
 * import org.springframework.web.bind.annotation.ModelAttribute;
 * 
 * import com.unah.ProyectoBD.Dtos.RegistroEstudianteDto;
 * 
 * @Controller // ¡Ojo! No @RestController
 * public class LoginController {
 * 
 * // Muestra la página de login
 * 
 * @GetMapping("/login")
 * public String showLoginForm() {
 * return "login"; // Devuelve el nombre del archivo login.html
 * }
 * 
 * // Muestra la página de registro
 * 
 * @GetMapping("/registro")
 * public String showRegistrationForm(org.springframework.ui.Model model) {
 * // Envía objetos vacíos al formulario para el data-binding
 * model.addAttribute("estudianteDto", new RegistroEstudianteDto());
 * // Podrías tener un DTO general para el registro
 * return "registro"; // Devuelve el nombre del archivo registro.html
 * }
 * 
 * // Procesa el registro de un estudiante (ejemplo)
 * 
 * @PostMapping("/registro/estudiante")
 * public String processStudentRegistration(@ModelAttribute("estudianteDto")
 * RegistroEstudianteDto dto) {
 * // Aquí llamas a tu servicio para guardar el nuevo estudiante
 * // userService.registrarEstudiante(dto);
 * return "redirect:/login?registro_exitoso";
 * }
 * 
 * // Controlador para redirigir según el rol del usuario
 * 
 * @GetMapping("/dashboard")
 * public String dashboard(Authentication authentication) {
 * // `Authentication` tiene la información del usuario logueado
 * if (authentication.getAuthorities().stream().anyMatch(a ->
 * a.getAuthority().equals("ESTUDIANTE"))) {
 * return "redirect:/tutorias/disponibles";
 * }
 * if (authentication.getAuthorities().stream().anyMatch(a ->
 * a.getAuthority().equals("TUTOR"))) {
 * return "redirect:/tutorias/mis-tutorias";
 * }
 * return "redirect:/login"; // Por si acaso
 * }
 * }
 */