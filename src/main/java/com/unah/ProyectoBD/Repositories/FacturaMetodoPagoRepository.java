package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.FacturaMetodoPagoModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class FacturaMetodoPagoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<FacturaMetodoPagoModel> rowMapper = (rs, rowNum) -> {
        FacturaMetodoPagoModel facturaMetodoPago = new FacturaMetodoPagoModel();
        facturaMetodoPago.setIdFacturaMetodoPago(rs.getInt("IdFacturaMetodoPago"));
        facturaMetodoPago.setIdFactura(rs.getInt("IdFactura"));
        facturaMetodoPago.setIdMetodoPago(rs.getInt("IdMetodoPago"));
        return facturaMetodoPago;
    };

    public FacturaMetodoPagoModel save(FacturaMetodoPagoModel facturaMetodoPago) {
        String sql = "INSERT INTO FacturaMetodoPago (IdFactura, IdMetodoPago) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, facturaMetodoPago.getIdFactura());
            ps.setInt(2, facturaMetodoPago.getIdMetodoPago());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            facturaMetodoPago.setIdFacturaMetodoPago(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return facturaMetodoPago;
    }

    public Optional<FacturaMetodoPagoModel> findById(Integer id) {
        String sql = "SELECT * FROM FacturaMetodoPago WHERE IdFacturaMetodoPago = ?";
        List<FacturaMetodoPagoModel> facturasMetodoPago = jdbcTemplate.query(sql, rowMapper, id);
        return facturasMetodoPago.isEmpty() ? Optional.empty() : Optional.of(facturasMetodoPago.get(0));
    }

    public List<FacturaMetodoPagoModel> findAll() {
        String sql = "SELECT * FROM FacturaMetodoPago";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(FacturaMetodoPagoModel facturaMetodoPago) {
        String sql = "UPDATE FacturaMetodoPago SET IdFactura = ?, IdMetodoPago = ? WHERE IdFacturaMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql,
                facturaMetodoPago.getIdFactura(), facturaMetodoPago.getIdMetodoPago(),
                facturaMetodoPago.getIdFacturaMetodoPago());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM FacturaMetodoPago WHERE IdFacturaMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
