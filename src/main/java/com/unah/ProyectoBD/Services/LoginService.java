package com.unah.ProyectoBD.Services;

import com.unah.ProyectoBD.Models.RolesModel;
import com.unah.ProyectoBD.Models.UsuariosModel;
import com.unah.ProyectoBD.Repositories.RolRepository;
import com.unah.ProyectoBD.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la base de datos por su correo
        UsuariosModel usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con el correo: " + correoInstitucional));

        // 2. Buscar los roles asociados a ese usuario por su ID
        List<RolesModel> roles = rolRepository.findRolesByIdUsuario(usuario.getIdUsuarios());

        // 3. Convertir la lista de roles a la que Spring Security entiende
        // (GrantedAuthority)
        List<GrantedAuthority> authorities = roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombreRol()))
                .collect(Collectors.toList());

        // 4. Devolver un objeto User que Spring Security puede usar para la
        // autenticación
        // Se le pasa el correo, la contraseña (ya hasheada en la BD) y sus
        // roles/autoridades.
        return new User(usuario.getCorreoInstitucional(), usuario.getUsuarioPassword(), authorities);
    }

    /**
     * NOTA IMPORTANTE:
     * Para que esto funcione, necesitas añadir el siguiente método en tu
     * `RolRepository`:
     *
     * public List<RolesModel> findRolesByIdUsuario(Integer idUsuario);
     *
     * Este método debe ejecutar una consulta como:
     * "SELECT * FROM Roles WHERE IdUsuario = ?"
     */
}