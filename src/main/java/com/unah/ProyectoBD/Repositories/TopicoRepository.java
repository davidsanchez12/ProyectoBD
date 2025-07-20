package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.TopicosModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TopicoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<TopicosModel> rowMapper = (rs, rowNum) -> {
        TopicosModel topico = new TopicosModel();
        topico.setIdTopicos(rs.getInt("IdTopicos"));
        topico.setIdAsignatura(rs.getInt("IdAsignatura"));
        topico.setIdTutor(rs.getInt("IdTutor"));
        topico.setNombre(rs.getString("Nombre"));
        return topico;
    };

    public TopicosModel save(TopicosModel topico) {
        String sql = "INSERT INTO Topicos (IdAsignatura, IdTutor, Nombre) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, topico.getIdAsignatura());
            ps.setInt(2, topico.getIdTutor());
            ps.setString(3, topico.getNombre());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            topico.setIdTopicos(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return topico;
    }

    public Optional<TopicosModel> findById(Integer id) {
        String sql = "SELECT * FROM Topicos WHERE IdTopicos = ?";
        List<TopicosModel> topicos = jdbcTemplate.query(sql, rowMapper, id);
        return topicos.isEmpty() ? Optional.empty() : Optional.of(topicos.get(0));
    }

    public Optional<TopicosModel> findByNombre(String nombre) {
        String sql = "SELECT * FROM Topicos WHERE Nombre = ?";
        List<TopicosModel> topicos = jdbcTemplate.query(sql, rowMapper, nombre);
        return topicos.isEmpty() ? Optional.empty() : Optional.of(topicos.get(0));
    }

    public List<TopicosModel> findAll() {
        String sql = "SELECT * FROM Topicos";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(TopicosModel topico) {
        String sql = "UPDATE Topicos SET IdAsignatura = ?, IdTutor = ?, Nombre = ? WHERE IdTopicos = ?";
        int affectedRows = jdbcTemplate.update(sql,
                topico.getIdAsignatura(), topico.getIdTutor(), topico.getNombre(), topico.getIdTopicos());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Topicos WHERE IdTopicos = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
