package com.unah.ProyectoBD.Repositories;

import com.unah.ProyectoBD.Models.FacturaModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class FacturaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<FacturaModel> rowMapper = (rs, rowNum) -> {
        FacturaModel factura = new FacturaModel();
        factura.setIdFacturas(rs.getInt("IdFacturas"));
        factura.setIdRangosCAI(rs.getInt("IdRangosCAI"));
        factura.setEmpresaEmisora(rs.getString("EmpresaEmisora"));
        factura.setRtnEmpresaEmisora(rs.getString("RTNEmpresaEmisora"));
        factura.setNumeroFactura(rs.getString("NumeroFactura"));
        factura.setEstablecimiento(rs.getInt("Establecimiento"));
        factura.setNombreCliente(rs.getString("NombreCliente"));
        factura.setRtnCliente(rs.getString("RTNCliente"));
        return factura;
    };

    public FacturaModel save(FacturaModel factura) {
        String sql = "INSERT INTO Facturas (IdRangosCAI, EmpresaEmisora, RTNEmpresaEmisora, NumeroFactura, Establecimiento, NombreCliente, RTNCliente) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, factura.getIdRangosCAI());
            ps.setString(2, factura.getEmpresaEmisora());
            ps.setString(3, factura.getRtnEmpresaEmisora());
            ps.setString(4, factura.getNumeroFactura());
            ps.setInt(5, factura.getEstablecimiento());
            ps.setString(6, factura.getNombreCliente());
            ps.setString(7, factura.getRtnCliente());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            factura.setIdFacturas(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return factura;
    }

    public Optional<FacturaModel> findById(Integer id) {
        String sql = "SELECT * FROM Facturas WHERE IdFacturas = ?";
        List<FacturaModel> facturas = jdbcTemplate.query(sql, rowMapper, id);
        return facturas.isEmpty() ? Optional.empty() : Optional.of(facturas.get(0));
    }

    public Optional<FacturaModel> findByNumeroFactura(String numeroFactura) {
        String sql = "SELECT * FROM Facturas WHERE NumeroFactura = ?";
        List<FacturaModel> facturas = jdbcTemplate.query(sql, rowMapper, numeroFactura);
        return facturas.isEmpty() ? Optional.empty() : Optional.of(facturas.get(0));
    }

    public List<FacturaModel> findAll() {
        String sql = "SELECT * FROM Facturas";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(FacturaModel factura) {
        String sql = "UPDATE Facturas SET IdRangosCAI = ?, EmpresaEmisora = ?, RTNEmpresaEmisora = ?, NumeroFactura = ?, Establecimiento = ?, NombreCliente = ?, RTNCliente = ? WHERE IdFacturas = ?";
        int affectedRows = jdbcTemplate.update(sql,
                factura.getIdRangosCAI(), factura.getEmpresaEmisora(), factura.getRtnEmpresaEmisora(),
                factura.getNumeroFactura(), factura.getEstablecimiento(), factura.getNombreCliente(),
                factura.getRtnCliente(), factura.getIdFacturas());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Facturas WHERE IdFacturas = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    /**
     * Busca el número de factura más alto para un rango CAI específico.
     * Esto ayuda a determinar el siguiente número correlativo.
     * 
     * @param idRangosCAI El ID del rango CAI.
     * @return El número de factura más alto como String, o Optional.empty() si no
     *         hay facturas para ese CAI.
     */
    public Optional<String> findLastNumeroFacturaByRangosCAIId(Integer idRangosCAI) {
        // Asumiendo que NumeroFactura tiene el formato "000-001-01-00000001"
        // Necesitamos extraer la parte numérica final para encontrar el máximo.
        // Esto puede variar dependiendo de cómo se manejen los números de factura.
        // Para simplificar, buscaremos el número de factura más alto alfabéticamente
        // que es un proxy para el valor numérico si el padding con ceros es
        // consistente.
        String sql = "SELECT TOP 1 NumeroFactura FROM Facturas WHERE IdRangosCAI = ? ORDER BY NumeroFactura DESC";
        try {
            String lastNumero = jdbcTemplate.queryForObject(sql, String.class, idRangosCAI);
            return Optional.ofNullable(lastNumero);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
