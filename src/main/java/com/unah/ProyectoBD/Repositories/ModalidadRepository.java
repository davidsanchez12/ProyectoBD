package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.ModalidadModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ModalidadRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<ModalidadModel> rowMapper = (rs, rowNum) -> {
        ModalidadModel modalidad = new ModalidadModel();
        modalidad.setIdModalidad(rs.getInt("IdModalidad"));
        modalidad.setTipoModalidad(rs.getString("TipoModalidad"));
        return modalidad;
    };

    public ModalidadModel save(ModalidadModel modalidad) {
        String sql = "INSERT INTO Modalidades (TipoModalidad) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, modalidad.getTipoModalidad());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            modalidad.setIdModalidad(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return modalidad;
    }

    public Optional<ModalidadModel> findById(Integer id) {
        String sql = "SELECT * FROM Modalidades WHERE IdModalidad = ?";
        List<ModalidadModel> modalidades = jdbcTemplate.query(sql, rowMapper, id);
        return modalidades.isEmpty() ? Optional.empty() : Optional.of(modalidades.get(0));
    }

    public Optional<ModalidadModel> findByTipoModalidad(String tipoModalidad) {
        String sql = "SELECT * FROM Modalidades WHERE TipoModalidad = ?";
        List<ModalidadModel> modalidades = jdbcTemplate.query(sql, rowMapper, tipoModalidad);
        return modalidades.isEmpty() ? Optional.empty() : Optional.of(modalidades.get(0));
    }

    public List<ModalidadModel> findAll() {
        String sql = "SELECT * FROM Modalidades";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(ModalidadModel modalidad) {
        String sql = "UPDATE Modalidades SET TipoModalidad = ? WHERE IdModalidad = ?";
        int affectedRows = jdbcTemplate.update(sql, modalidad.getTipoModalidad(), modalidad.getIdModalidad());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Modalidades WHERE IdModalidad = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}