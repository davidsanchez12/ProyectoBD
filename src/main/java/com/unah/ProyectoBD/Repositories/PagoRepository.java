package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.PagoModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PagoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<PagoModel> rowMapper = (rs, rowNum) -> {
        PagoModel pago = new PagoModel();
        pago.setIdPagos(rs.getInt("IdPagos"));
        pago.setOrdenPago(rs.getString("OrdenPago"));
        pago.setReciboDocente(rs.getString("ReciboDocente"));
        pago.setComprobantePago(rs.getString("ComprobantePago"));
        pago.setMonto(rs.getBigDecimal("Monto"));
        pago.setIdTutor(rs.getInt("IdTutor"));
        return pago;
    };

    public PagoModel save(PagoModel pago) {
        String sql = "INSERT INTO Pagos (OrdenPago, ReciboDocente, ComprobantePago, Monto, IdTutor) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pago.getOrdenPago());
            ps.setString(2, pago.getReciboDocente());
            ps.setString(3, pago.getComprobantePago());
            ps.setBigDecimal(4, pago.getMonto());
            ps.setObject(5, pago.getIdTutor()); // Puede ser null
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            pago.setIdPagos(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return pago;
    }

    public Optional<PagoModel> findById(Integer id) {
        String sql = "SELECT * FROM Pagos WHERE IdPagos = ?";
        List<PagoModel> pagos = jdbcTemplate.query(sql, rowMapper, id);
        return pagos.isEmpty() ? Optional.empty() : Optional.of(pagos.get(0));
    }

    public Optional<PagoModel> findByOrdenPago(String ordenPago) {
        String sql = "SELECT * FROM Pagos WHERE OrdenPago = ?";
        List<PagoModel> pagos = jdbcTemplate.query(sql, rowMapper, ordenPago);
        return pagos.isEmpty() ? Optional.empty() : Optional.of(pagos.get(0));
    }

    public Optional<PagoModel> findByReciboDocente(String reciboDocente) {
        String sql = "SELECT * FROM Pagos WHERE ReciboDocente = ?";
        List<PagoModel> pagos = jdbcTemplate.query(sql, rowMapper, reciboDocente);
        return pagos.isEmpty() ? Optional.empty() : Optional.of(pagos.get(0));
    }

    public Optional<PagoModel> findByComprobantePago(String comprobantePago) {
        String sql = "SELECT * FROM Pagos WHERE ComprobantePago = ?";
        List<PagoModel> pagos = jdbcTemplate.query(sql, rowMapper, comprobantePago);
        return pagos.isEmpty() ? Optional.empty() : Optional.of(pagos.get(0));
    }

    public List<PagoModel> findAll() {
        String sql = "SELECT * FROM Pagos";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(PagoModel pago) {
        String sql = "UPDATE Pagos SET OrdenPago = ?, ReciboDocente = ?, ComprobantePago = ?, Monto = ?, IdTutor = ? WHERE IdPagos = ?";
        int affectedRows = jdbcTemplate.update(sql,
                pago.getOrdenPago(), pago.getReciboDocente(), pago.getComprobantePago(), pago.getMonto(),
                pago.getIdTutor(), pago.getIdPagos());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Pagos WHERE IdPagos = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
