package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.MetodoPagoModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class MetodoPagoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<MetodoPagoModel> rowMapper = (rs, rowNum) -> {
        MetodoPagoModel metodoPago = new MetodoPagoModel();
        metodoPago.setIdMetodoPago(rs.getInt("IdMetodoPago"));
        metodoPago.setTipoMetodo(rs.getString("TipoMetodo"));
        return metodoPago;
    };

    public MetodoPagoModel save(MetodoPagoModel metodoPago) {
        String sql = "INSERT INTO MetodoPago (TipoMetodo) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, metodoPago.getTipoMetodo());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            metodoPago.setIdMetodoPago(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return metodoPago;
    }

    public Optional<MetodoPagoModel> findById(Integer id) {
        String sql = "SELECT * FROM MetodoPago WHERE IdMetodoPago = ?";
        List<MetodoPagoModel> metodos = jdbcTemplate.query(sql, rowMapper, id);
        return metodos.isEmpty() ? Optional.empty() : Optional.of(metodos.get(0));
    }

    public Optional<MetodoPagoModel> findByTipoMetodo(String tipoMetodo) {
        String sql = "SELECT * FROM MetodoPago WHERE TipoMetodo = ?";
        List<MetodoPagoModel> metodos = jdbcTemplate.query(sql, rowMapper, tipoMetodo);
        return metodos.isEmpty() ? Optional.empty() : Optional.of(metodos.get(0));
    }

    public List<MetodoPagoModel> findAll() {
        String sql = "SELECT * FROM MetodoPago";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(MetodoPagoModel metodoPago) {
        String sql = "UPDATE MetodoPago SET TipoMetodo = ? WHERE IdMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql, metodoPago.getTipoMetodo(), metodoPago.getIdMetodoPago());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM MetodoPago WHERE IdMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
