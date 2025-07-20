package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.AsignaturaModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AsignaturaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<AsignaturaModel> rowMapper = (rs, rowNum) -> {
        AsignaturaModel asignatura = new AsignaturaModel();
        asignatura.setIdAsignatura(rs.getInt("IdAsignatura"));
        asignatura.setNombreAsignatura(rs.getString("NombreAsignatura"));
        asignatura.setCodigoAsignatura(rs.getString("CodigoAsignatura"));
        asignatura.setIdTutor(rs.getInt("IdTutor"));
        return asignatura;
    };

    public AsignaturaModel save(AsignaturaModel asignatura) {
        String sql = "INSERT INTO Asignaturas (NombreAsignatura, CodigoAsignatura, IdTutor) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, asignatura.getNombreAsignatura());
            ps.setString(2, asignatura.getCodigoAsignatura());
            ps.setObject(3, asignatura.getIdTutor()); // Puede ser null
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            asignatura.setIdAsignatura(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return asignatura;
    }

    public Optional<AsignaturaModel> findById(Integer id) {
        String sql = "SELECT * FROM Asignaturas WHERE IdAsignatura = ?";
        List<AsignaturaModel> asignaturas = jdbcTemplate.query(sql, rowMapper, id);
        return asignaturas.isEmpty() ? Optional.empty() : Optional.of(asignaturas.get(0));
    }

    public Optional<AsignaturaModel> findByNombreAsignatura(String nombreAsignatura) {
        String sql = "SELECT * FROM Asignaturas WHERE NombreAsignatura = ?";
        List<AsignaturaModel> asignaturas = jdbcTemplate.query(sql, rowMapper, nombreAsignatura);
        return asignaturas.isEmpty() ? Optional.empty() : Optional.of(asignaturas.get(0));
    }

    public Optional<AsignaturaModel> findByCodigoAsignatura(String codigoAsignatura) {
        String sql = "SELECT * FROM Asignaturas WHERE CodigoAsignatura = ?";
        List<AsignaturaModel> asignaturas = jdbcTemplate.query(sql, rowMapper, codigoAsignatura);
        return asignaturas.isEmpty() ? Optional.empty() : Optional.of(asignaturas.get(0));
    }

    public List<AsignaturaModel> findAll() {
        String sql = "SELECT * FROM Asignaturas";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(AsignaturaModel asignatura) {
        String sql = "UPDATE Asignaturas SET NombreAsignatura = ?, CodigoAsignatura = ?, IdTutor = ? WHERE IdAsignatura = ?";
        int affectedRows = jdbcTemplate.update(sql,
                asignatura.getNombreAsignatura(), asignatura.getCodigoAsignatura(), asignatura.getIdTutor(),
                asignatura.getIdAsignatura());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Asignaturas WHERE IdAsignatura = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
