package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.InscripcionModel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InscripcionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<InscripcionModel> rowMapper = (rs, rowNum) -> {
        InscripcionModel inscripcion = new InscripcionModel();
        inscripcion.setIdInscripcion(rs.getInt("IdInscripcion"));
        inscripcion.setIdEstudiante(rs.getInt("IdEstudiante"));
        inscripcion.setIdTutoria(rs.getInt("IdTutoria"));
        inscripcion.setFechaInscripcion(rs.getDate("FechaInscripcion").toLocalDate());
        return inscripcion;
    };

    public InscripcionModel save(InscripcionModel inscripcion) {
        String sql = "INSERT INTO Inscripcion (IdEstudiante, IdTutoria, FechaInscripcion) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdTutoria());
            ps.setDate(3, Date.valueOf(inscripcion.getFechaInscripcion()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            inscripcion.setIdInscripcion(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return inscripcion;
    }

    public Optional<InscripcionModel> findById(Integer id) {
        String sql = "SELECT * FROM Inscripcion WHERE IdInscripcion = ?";
        List<InscripcionModel> inscripciones = jdbcTemplate.query(sql, rowMapper, id);
        return inscripciones.isEmpty() ? Optional.empty() : Optional.of(inscripciones.get(0));
    }

    public List<InscripcionModel> findAll() {
        String sql = "SELECT * FROM Inscripcion";
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Cuenta el número de inscripciones para una tutoría específica.
     * 
     * @param idTutoria El ID de la tutoría.
     * @return El número de inscripciones para esa tutoría.
     */
    public int countInscripcionesByTutoriaId(Integer idTutoria) {
        String sql = "SELECT COUNT(*) FROM Inscripcion WHERE IdTutoria = ?";
        // queryForObject se usa cuando se espera un único valor de retorno (como un
        // COUNT)
        return jdbcTemplate.queryForObject(sql, Integer.class, idTutoria);
    }

    /**
     * Verifica si un estudiante ya está inscrito en una tutoría específica.
     * 
     * @param idEstudiante El ID del estudiante.
     * @param idTutoria    El ID de la tutoría.
     * @return true si el estudiante ya está inscrito, false en caso contrario.
     */
    public boolean existsByEstudianteIdAndTutoriaId(Integer idEstudiante, Integer idTutoria) {
        String sql = "SELECT COUNT(*) FROM Inscripcion WHERE IdEstudiante = ? AND IdTutoria = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idEstudiante, idTutoria);
        return count != null && count > 0;
    }

    public boolean update(InscripcionModel inscripcion) {
        String sql = "UPDATE Inscripcion SET IdEstudiante = ?, IdTutoria = ?, FechaInscripcion = ? WHERE IdInscripcion = ?";
        int affectedRows = jdbcTemplate.update(sql,
                inscripcion.getIdEstudiante(), inscripcion.getIdTutoria(),
                Date.valueOf(inscripcion.getFechaInscripcion()), inscripcion.getIdInscripcion());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Inscripcion WHERE IdInscripcion = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
