package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.EstudianteModel;
import com.unah.ProyectoBD.Models.TutoresModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TutorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<TutoresModel> rowMapper = (rs, rowNum) -> {
        TutoresModel tutor = new TutoresModel();
        tutor.setIdTutor(rs.getInt("IdTutor"));
        tutor.setIdPersona(rs.getInt("IdPersona"));
        tutor.setIdUsuario(rs.getInt("IdUsuario"));
        tutor.setSalarioPorTutoria(rs.getBigDecimal("SalarioPorTutoria"));
        return tutor;
    };

    public TutoresModel save(TutoresModel tutor) {
        String sql = "INSERT INTO Tutores (IdPersona, IdUsuario, SalarioPorTutoria) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tutor.getIdPersona());
            ps.setInt(2, tutor.getIdUsuario());
            ps.setBigDecimal(3, tutor.getSalarioPorTutoria());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            tutor.setIdTutor(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return tutor;
    }

    public Optional<TutoresModel> findById(Integer id) {
        String sql = "SELECT * FROM Tutores WHERE IdTutor = ?";
        List<TutoresModel> tutores = jdbcTemplate.query(sql, rowMapper, id);
        return tutores.isEmpty() ? Optional.empty() : Optional.of(tutores.get(0));
    }

    public List<TutoresModel> findAll() {
        String sql = "SELECT * FROM Tutores";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(TutoresModel tutor) {
        String sql = "UPDATE Tutores SET IdPersona = ?, IdUsuario = ?, SalarioPorTutoria = ? WHERE IdTutor = ?";
        int affectedRows = jdbcTemplate.update(sql,
                tutor.getIdPersona(), tutor.getIdUsuario(), tutor.getSalarioPorTutoria(), tutor.getIdTutor());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Tutores WHERE IdTutor = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    public Optional<TutoresModel> findByCorreo(String correo) {
        // La consulta SQL une Estudiantes con Usuarios para encontrar al estudiante por
        // el email
        String sql = "SELECT e.* FROM Tutores e " +
                "JOIN Usuarios u ON e.IdUsuario = u.IdUsuarios " +
                "WHERE u.CorreoInstitucional = ?";

        List<TutoresModel> tutores = jdbcTemplate.query(sql, rowMapper, correo);

        // Si la lista no está vacía, devuelve el primer (y único) elemento.
        // Si no, devuelve un Optional vacío.
        return tutores.isEmpty() ? Optional.empty() : Optional.of(tutores.get(0));
    }

}
