package com.unah.ProyectoBD.Repositories;

import com.unah.ProyectoBD.Models.UsuariosModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper para mapear un ResultSet a un objeto Usuario
    private RowMapper<UsuariosModel> rowMapper = (rs, rowNum) -> {
        UsuariosModel usuario = new UsuariosModel();
        usuario.setIdUsuarios(rs.getInt("IdUsuarios"));
        usuario.setCorreoInstitucional(rs.getString("CorreoInstitucional"));
        usuario.setUsuarioPassword(rs.getString("UsuarioPassword"));
        return usuario;
    };

    /**
     * Guarda un nuevo usuario en la base de datos.
     * 
     * @param usuario El objeto Usuario a guardar.
     * @return El objeto Usuario guardado con su ID generado.
     */
    public UsuariosModel save(UsuariosModel usuario) {
        String sql = "INSERT INTO Usuarios (CorreoInstitucional, UsuarioPassword) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getCorreoInstitucional());
            ps.setString(2, usuario.getUsuarioPassword());
            return ps;
        }, keyHolder);

        // Obtener el ID generado por la base de datos
        if (keyHolder.getKeys() != null) {
            usuario.setIdUsuarios(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return usuario;
    }

    /**
     * Encuentra un usuario por su ID.
     * 
     * @param id El ID del usuario.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    public Optional<UsuariosModel> findById(Integer id) {
        String sql = "SELECT * FROM Usuarios WHERE IdUsuarios = ?";
        List<UsuariosModel> usuarios = jdbcTemplate.query(sql, rowMapper, id);
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    /**
     * Encuentra un usuario por su correo institucional.
     * 
     * @param correoInstitucional El correo institucional del usuario.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    public Optional<UsuariosModel> findByCorreoInstitucional(String correoInstitucional) {
        String sql = "SELECT * FROM Usuarios WHERE CorreoInstitucional = ?";
        List<UsuariosModel> usuarios = jdbcTemplate.query(sql, rowMapper, correoInstitucional);
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    /**
     * Obtiene todos los usuarios.
     * 
     * @return Una lista de todos los usuarios.
     */
    public List<UsuariosModel> findAll() {
        String sql = "SELECT * FROM Usuarios";
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param usuario El objeto Usuario con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean update(UsuariosModel usuario) {
        String sql = "UPDATE Usuarios SET CorreoInstitucional = ?, UsuarioPassword = ? WHERE IdUsuarios = ?";
        int affectedRows = jdbcTemplate.update(sql, usuario.getCorreoInstitucional(), usuario.getUsuarioPassword(),
                usuario.getIdUsuarios());
        return affectedRows > 0;
    }

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id El ID del usuario a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Usuarios WHERE IdUsuarios = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
