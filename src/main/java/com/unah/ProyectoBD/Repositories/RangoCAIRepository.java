package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.RangosCAIModel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class RangoCAIRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper para mapear un ResultSet a un objeto RangoCAI.
     * Asegúrate de que los nombres de las columnas ('IdRangosCai', 'CAI',
     * 'RangoInicial', 'RangoFinal', 'FechaLimiteEmision')
     * coincidan exactamente con los de tu tabla en la base de datos, incluyendo
     * mayúsculas y minúsculas.
     */
    private RowMapper<RangosCAIModel> rowMapper = (rs, rowNum) -> {
        RangosCAIModel rangoCAI = new RangosCAIModel();
        rangoCAI.setIdRangosCai(rs.getInt("IdRangosCai"));
        rangoCAI.setCai(rs.getString("CAI"));
        rangoCAI.setRangoInicial(rs.getString("RangoInicial"));
        rangoCAI.setRangoFinal(rs.getString("RangoFinal"));
        // Manejo defensivo para FechaLimiteEmision, aunque sea NOT NULL en la DB
        Date sqlDate = rs.getDate("FechaLimiteEmision");
        rangoCAI.setFechaLimiteEmision(sqlDate != null ? sqlDate.toLocalDate() : null);
        return rangoCAI;
    };

    public RangosCAIModel save(RangosCAIModel rangoCAI) {
        String sql = "INSERT INTO RangosCAI (CAI, RangoInicial, RangoFinal, FechaLimiteEmision) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, rangoCAI.getCai());
            ps.setString(2, rangoCAI.getRangoInicial());
            ps.setString(3, rangoCAI.getRangoFinal());
            ps.setDate(4, Date.valueOf(rangoCAI.getFechaLimiteEmision()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            rangoCAI.setIdRangosCai(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return rangoCAI;
    }

    public Optional<RangosCAIModel> findById(Integer id) {
        String sql = "SELECT * FROM RangosCAI WHERE IdRangosCai = ?";
        List<RangosCAIModel> rangos = jdbcTemplate.query(sql, rowMapper, id);
        return rangos.isEmpty() ? Optional.empty() : Optional.of(rangos.get(0));
    }

    public Optional<RangosCAIModel> findByCai(String cai) {
        String sql = "SELECT * FROM RangosCAI WHERE CAI = ?";
        List<RangosCAIModel> rangos = jdbcTemplate.query(sql, rowMapper, cai);
        return rangos.isEmpty() ? Optional.empty() : Optional.of(rangos.get(0));
    }

    public List<RangosCAIModel> findAll() {
        String sql = "SELECT * FROM RangosCAI";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(RangosCAIModel rangoCAI) {
        String sql = "UPDATE RangosCAI SET CAI = ?, RangoInicial = ?, RangoFinal = ?, FechaLimiteEmision = ? WHERE IdRangosCai = ?";
        int affectedRows = jdbcTemplate.update(sql,
                rangoCAI.getCai(), rangoCAI.getRangoInicial(), rangoCAI.getRangoFinal(),
                Date.valueOf(rangoCAI.getFechaLimiteEmision()), rangoCAI.getIdRangosCai());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM RangosCAI WHERE IdRangosCai = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    /**
     * Encuentra el Rango CAI activo (el que tiene la fecha límite de emisión más
     * lejana en el futuro).
     * En un sistema real, podría haber una columna 'activo' o una lógica más
     * compleja.
     * 
     * @return Un Optional que contiene el RangoCAI activo, o vacío si no hay
     *         ninguno.
     */
    public Optional<RangosCAIModel> findActiveRangoCAI() {
        // Ordena por FechaLimiteEmision de forma descendente para obtener el más
        // reciente/válido
        // y toma el primero. Esto asume que el CAI más 'nuevo' es el activo.
        String sql = "SELECT TOP 1 * FROM RangosCAI WHERE FechaLimiteEmision >= ? ORDER BY FechaLimiteEmision DESC";
        List<RangosCAIModel> rangos = jdbcTemplate.query(sql, rowMapper, LocalDate.now());
        return rangos.isEmpty() ? Optional.empty() : Optional.of(rangos.get(0));
    }
}
