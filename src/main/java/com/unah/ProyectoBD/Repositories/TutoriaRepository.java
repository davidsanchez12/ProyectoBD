package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.TutoriaModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TutoriaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<TutoriaModel> rowMapper = (rs, rowNum) -> {
        TutoriaModel tutoria = new TutoriaModel();
        tutoria.setIdTutoria(rs.getInt("IdTutoria"));
        tutoria.setIdTutor(rs.getInt("IdTutor"));
        tutoria.setIdTopico(rs.getInt("IdTopico"));
        tutoria.setHoraIncio(rs.getString("HoraIncio"));
        tutoria.setHoraFin(rs.getString("HoraFin"));
        tutoria.setPrecio(rs.getBigDecimal("Precio"));
        tutoria.setIdModalidad(rs.getInt("IdModalidad"));
        tutoria.setLimiteEstudiantes(rs.getInt("LimiteEstudiantes"));
        return tutoria;
    };

    public TutoriaModel save(TutoriaModel tutoria) {
        String sql = "INSERT INTO Tutorias (IdTutor, IdTopico, HoraIncio, HoraFin, Precio, IdModalidad, LimiteEstudiantes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tutoria.getIdTutor());
            ps.setInt(2, tutoria.getIdTopico());
            ps.setString(3, tutoria.getHoraIncio());
            ps.setString(4, tutoria.getHoraFin());
            ps.setBigDecimal(5, tutoria.getPrecio());
            ps.setInt(6, tutoria.getIdModalidad());
            ps.setObject(7, tutoria.getLimiteEstudiantes()); // Puede ser null
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            tutoria.setIdTutoria(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return tutoria;
    }

    public Optional<TutoriaModel> findById(Integer id) {
        String sql = "SELECT * FROM Tutorias WHERE IdTutoria = ?";
        List<TutoriaModel> tutorias = jdbcTemplate.query(sql, rowMapper, id);
        return tutorias.isEmpty() ? Optional.empty() : Optional.of(tutorias.get(0));
    }

    public List<TutoriaModel> findAll() {
        String sql = "SELECT * FROM Tutorias";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(TutoriaModel tutoria) {
        String sql = "UPDATE Tutorias SET IdTutor = ?, IdTopico = ?, HoraIncio = ?, HoraFin = ?, Precio = ?, IdModalidad = ?, LimiteEstudiantes = ? WHERE IdTutoria = ?";
        int affectedRows = jdbcTemplate.update(sql,
                tutoria.getIdTutor(), tutoria.getIdTopico(), tutoria.getHoraIncio(), tutoria.getHoraFin(),
                tutoria.getPrecio(), tutoria.getIdModalidad(), tutoria.getLimiteEstudiantes(), tutoria.getIdTutoria());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Tutorias WHERE IdTutoria = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
