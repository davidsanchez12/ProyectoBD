package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.PagoMetodoPagoModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PagosMetodoPagoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<PagoMetodoPagoModel> rowMapper = (rs, rowNum) -> {
        PagoMetodoPagoModel pagosMetodoPago = new PagoMetodoPagoModel();
        pagosMetodoPago.setIdPagosMetodoPago(rs.getInt("IdPagosMetodoPago"));
        pagosMetodoPago.setIdPagos(rs.getInt("IdPagos"));
        pagosMetodoPago.setIdMetodoPago(rs.getInt("IdMetodoPago"));
        return pagosMetodoPago;
    };

    public PagoMetodoPagoModel save(PagoMetodoPagoModel pagosMetodoPago) {
        String sql = "INSERT INTO PagosMetodoPago (IdPagos, IdMetodoPago) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pagosMetodoPago.getIdPagos());
            ps.setInt(2, pagosMetodoPago.getIdMetodoPago());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            pagosMetodoPago.setIdPagosMetodoPago(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return pagosMetodoPago;
    }

    public Optional<PagoMetodoPagoModel> findById(Integer id) {
        String sql = "SELECT * FROM PagosMetodoPago WHERE IdPagosMetodoPago = ?";
        List<PagoMetodoPagoModel> pagosMetodoPago = jdbcTemplate.query(sql, rowMapper, id);
        return pagosMetodoPago.isEmpty() ? Optional.empty() : Optional.of(pagosMetodoPago.get(0));
    }

    public List<PagoMetodoPagoModel> findAll() {
        String sql = "SELECT * FROM PagosMetodoPago";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(PagoMetodoPagoModel pagosMetodoPago) {
        String sql = "UPDATE PagosMetodoPago SET IdPagos = ?, IdMetodoPago = ? WHERE IdPagosMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql,
                pagosMetodoPago.getIdPagos(), pagosMetodoPago.getIdMetodoPago(),
                pagosMetodoPago.getIdPagosMetodoPago());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM PagosMetodoPago WHERE IdPagosMetodoPago = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
