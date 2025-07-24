package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.EstudianteModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EstudianteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<EstudianteModel> rowMapper = (rs, rowNum) -> {
        EstudianteModel estudiante = new EstudianteModel();
        estudiante.setIdEstudiante(rs.getInt("IdEstudiante"));
        estudiante.setIdPersona(rs.getInt("IdPersona"));
        estudiante.setIdUsuario(rs.getInt("IdUsuario"));
        estudiante.setNumeroCuenta(rs.getString("NumeroCuenta"));
        return estudiante;
    };

    public EstudianteModel save(EstudianteModel estudiante) {
        String sql = "INSERT INTO Estudiantes (IdPersona, IdUsuario, NumeroCuenta) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, estudiante.getIdPersona());
            ps.setInt(2, estudiante.getIdUsuario());
            ps.setString(3, estudiante.getNumeroCuenta());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            estudiante.setIdEstudiante(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return estudiante;
    }

    public Optional<EstudianteModel> findById(Integer id) {
        String sql = "SELECT * FROM Estudiantes WHERE IdEstudiante = ?";
        List<EstudianteModel> estudiantes = jdbcTemplate.query(sql, rowMapper, id);
        return estudiantes.isEmpty() ? Optional.empty() : Optional.of(estudiantes.get(0));
    }

    public Optional<EstudianteModel> findByNumeroCuenta(String numeroCuenta) {
        String sql = "SELECT * FROM Estudiantes WHERE NumeroCuenta = ?";
        List<EstudianteModel> estudiantes = jdbcTemplate.query(sql, rowMapper, numeroCuenta);
        return estudiantes.isEmpty() ? Optional.empty() : Optional.of(estudiantes.get(0));
    }

    public List<EstudianteModel> findAll() {
        String sql = "SELECT * FROM Estudiantes";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(EstudianteModel estudiante) {
        String sql = "UPDATE Estudiantes SET IdPersona = ?, IdUsuario = ?, NumeroCuenta = ? WHERE IdEstudiante = ?";
        int affectedRows = jdbcTemplate.update(sql,
                estudiante.getIdPersona(), estudiante.getIdUsuario(), estudiante.getNumeroCuenta(),
                estudiante.getIdEstudiante());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Estudiantes WHERE IdEstudiante = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    public Optional<EstudianteModel> findByCorreo(String correo) {
        // La consulta SQL une Estudiantes con Usuarios para encontrar al estudiante por
        // el email
        String sql = "SELECT e.* FROM Estudiantes e " +
                "JOIN Usuarios u ON e.IdUsuario = u.IdUsuarios " +
                "WHERE u.CorreoInstitucional = ?";

        List<EstudianteModel> estudiantes = jdbcTemplate.query(sql, rowMapper, correo);

        // Si la lista no está vacía, devuelve el primer (y único) elemento.
        // Si no, devuelve un Optional vacío.
        return estudiantes.isEmpty() ? Optional.empty() : Optional.of(estudiantes.get(0));
    }

}
