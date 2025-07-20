package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.EstablecimientoModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EstablecimientoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<EstablecimientoModel> rowMapper = (rs, rowNum) -> {
        EstablecimientoModel establecimiento = new EstablecimientoModel();
        establecimiento.setIdEstablecimiento(rs.getInt("IdEstablecimiento"));
        establecimiento.setNombreEstablecimiento(rs.getString("NombreEstablecimiento"));
        establecimiento.setDireccionEstablecimiento(rs.getString("DireccionEstablecimiento"));
        establecimiento.setTelefonoEstablecimiento(rs.getString("TelefonoEstablecimiento"));
        return establecimiento;
    };

    public EstablecimientoModel save(EstablecimientoModel establecimiento) {
        String sql = "INSERT INTO Establecimiento (NombreEstablecimiento, DireccionEstablecimiento, TelefonoEstablecimiento) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, establecimiento.getNombreEstablecimiento());
            ps.setString(2, establecimiento.getDireccionEstablecimiento());
            ps.setString(3, establecimiento.getTelefonoEstablecimiento());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            establecimiento.setIdEstablecimiento(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return establecimiento;
    }

    public Optional<EstablecimientoModel> findById(Integer id) {
        String sql = "SELECT * FROM Establecimiento WHERE IdEstablecimiento = ?";
        List<EstablecimientoModel> establecimientos = jdbcTemplate.query(sql, rowMapper, id);
        return establecimientos.isEmpty() ? Optional.empty() : Optional.of(establecimientos.get(0));
    }

    public List<EstablecimientoModel> findAll() {
        String sql = "SELECT * FROM Establecimiento";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(EstablecimientoModel establecimiento) {
        String sql = "UPDATE Establecimiento SET NombreEstablecimiento = ?, DireccionEstablecimiento = ?, TelefonoEstablecimiento = ? WHERE IdEstablecimiento = ?";
        int affectedRows = jdbcTemplate.update(sql,
                establecimiento.getNombreEstablecimiento(), establecimiento.getDireccionEstablecimiento(),
                establecimiento.getTelefonoEstablecimiento(), establecimiento.getIdEstablecimiento());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Establecimiento WHERE IdEstablecimiento = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}