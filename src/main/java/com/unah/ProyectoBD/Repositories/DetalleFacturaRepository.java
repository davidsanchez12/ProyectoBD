package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.DetalleFacturaModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DetalleFacturaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<DetalleFacturaModel> rowMapper = (rs, rowNum) -> {
        DetalleFacturaModel detalleFactura = new DetalleFacturaModel();
        detalleFactura.setIdDetalleFactura(rs.getInt("IdDetalleFactura"));
        detalleFactura.setIdFactura(rs.getInt("IdFactura"));
        detalleFactura.setIdTutoria(rs.getInt("IdTutoria"));
        detalleFactura.setDescripcion(rs.getString("Descripcion"));
        detalleFactura.setCantidad(rs.getInt("Cantidad"));
        detalleFactura.setSubtotal(rs.getBigDecimal("Subtotal"));
        detalleFactura.setTotal(rs.getBigDecimal("Total"));
        return detalleFactura;
    };

    public DetalleFacturaModel save(DetalleFacturaModel detalleFactura) {
        String sql = "INSERT INTO DetalleFactura (IdFactura, IdTutoria, Descripcion, Cantidad, Subtotal, Total) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, detalleFactura.getIdFactura());
            ps.setInt(2, detalleFactura.getIdTutoria());
            ps.setString(3, detalleFactura.getDescripcion());
            ps.setInt(4, detalleFactura.getCantidad());
            ps.setBigDecimal(5, detalleFactura.getSubtotal());
            ps.setBigDecimal(6, detalleFactura.getTotal());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            detalleFactura.setIdDetalleFactura(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return detalleFactura;
    }

    public Optional<DetalleFacturaModel> findById(Integer id) {
        String sql = "SELECT * FROM DetalleFactura WHERE IdDetalleFactura = ?";
        List<DetalleFacturaModel> detalles = jdbcTemplate.query(sql, rowMapper, id);
        return detalles.isEmpty() ? Optional.empty() : Optional.of(detalles.get(0));
    }

    public List<DetalleFacturaModel> findAll() {
        String sql = "SELECT * FROM DetalleFactura";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(DetalleFacturaModel detalleFactura) {
        String sql = "UPDATE DetalleFactura SET IdFactura = ?, IdTutoria = ?, Descripcion = ?, Cantidad = ?, Subtotal = ?, Total = ? WHERE IdDetalleFactura = ?";
        int affectedRows = jdbcTemplate.update(sql,
                detalleFactura.getIdFactura(), detalleFactura.getIdTutoria(), detalleFactura.getDescripcion(),
                detalleFactura.getCantidad(), detalleFactura.getSubtotal(), detalleFactura.getTotal(),
                detalleFactura.getIdDetalleFactura());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM DetalleFactura WHERE IdDetalleFactura = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
