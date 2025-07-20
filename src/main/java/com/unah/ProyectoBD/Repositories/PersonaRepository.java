package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.PersonasModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PersonaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<PersonasModel> rowMapper = (rs, rowNum) -> {
        PersonasModel persona = new PersonasModel();
        persona.setIdPersonas(rs.getInt("IdPersonas"));
        persona.setPrimerNombre(rs.getString("PrimerNombre"));
        persona.setSegundoNombre(rs.getString("SegundoNombre"));
        persona.setPrimerApellido(rs.getString("PrimerApellido"));
        persona.setSegundoApellido(rs.getString("SegundoApellido"));
        persona.setDni(rs.getString("Dni"));
        persona.setRtn(rs.getString("Rtn"));
        persona.setTelefono(rs.getString("Telefono"));
        persona.setCorreoInstitucional(rs.getString("CorreoInstitucional"));
        return persona;
    };

    public PersonasModel save(PersonasModel persona) {
        String sql = "INSERT INTO Personas (PrimerNombre, SegundoNombre, PrimerApellido, SegundoApellido, Dni, Rtn, Telefono, CorreoInstitucional) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, persona.getPrimerNombre());
            ps.setString(2, persona.getSegundoNombre());
            ps.setString(3, persona.getPrimerApellido());
            ps.setString(4, persona.getSegundoApellido());
            ps.setString(5, persona.getDni());
            ps.setString(6, persona.getRtn());
            ps.setString(7, persona.getTelefono());
            ps.setString(8, persona.getCorreoInstitucional());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            persona.setIdPersonas(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return persona;
    }

    public Optional<PersonasModel> findById(Integer id) {
        String sql = "SELECT * FROM Personas WHERE IdPersonas = ?";
        List<PersonasModel> personas = jdbcTemplate.query(sql, rowMapper, id);
        return personas.isEmpty() ? Optional.empty() : Optional.of(personas.get(0));
    }

    public Optional<PersonasModel> findByDni(String dni) {
        String sql = "SELECT * FROM Personas WHERE Dni = ?";
        List<PersonasModel> personas = jdbcTemplate.query(sql, rowMapper, dni);
        return personas.isEmpty() ? Optional.empty() : Optional.of(personas.get(0));
    }

    public Optional<PersonasModel> findByRtn(String rtn) {
        String sql = "SELECT * FROM Personas WHERE Rtn = ?";
        List<PersonasModel> personas = jdbcTemplate.query(sql, rowMapper, rtn);
        return personas.isEmpty() ? Optional.empty() : Optional.of(personas.get(0));
    }

    public Optional<PersonasModel> findByCorreoInstitucional(String correoInstitucional) {
        String sql = "SELECT * FROM Personas WHERE CorreoInstitucional = ?";
        List<PersonasModel> personas = jdbcTemplate.query(sql, rowMapper, correoInstitucional);
        return personas.isEmpty() ? Optional.empty() : Optional.of(personas.get(0));
    }

    public List<PersonasModel> findAll() {
        String sql = "SELECT * FROM Personas";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(PersonasModel persona) {
        String sql = "UPDATE Personas SET PrimerNombre = ?, SegundoNombre = ?, PrimerApellido = ?, SegundoApellido = ?, Dni = ?, Rtn = ?, Telefono = ?, CorreoInstitucional = ? WHERE IdPersonas = ?";
        int affectedRows = jdbcTemplate.update(sql,
                persona.getPrimerNombre(), persona.getSegundoNombre(), persona.getPrimerApellido(),
                persona.getSegundoApellido(), persona.getDni(), persona.getRtn(),
                persona.getTelefono(), persona.getCorreoInstitucional(), persona.getIdPersonas());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Personas WHERE IdPersonas = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
