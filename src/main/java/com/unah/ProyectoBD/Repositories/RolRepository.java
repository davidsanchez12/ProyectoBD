package com.unah.ProyectoBD.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unah.ProyectoBD.Models.RolesModel;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class RolRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<RolesModel> rowMapper = (rs, rowNum) -> {
        RolesModel rol = new RolesModel();
        rol.setIdRol(rs.getInt("IdRol"));
        rol.setNombreRol(rs.getString("NombreRol"));
        rol.setIdUsuario(rs.getInt("IdUsuario"));
        return rol;
    };

    public RolesModel save(RolesModel rol) {
        String sql = "INSERT INTO Roles (NombreRol, IdUsuario) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, rol.getNombreRol());
            ps.setObject(2, rol.getIdUsuario()); // Puede ser null si no hay un usuario asociado inicialmente
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            rol.setIdRol(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return rol;
    }

    public Optional<RolesModel> findById(Integer id) {
        String sql = "SELECT * FROM Roles WHERE IdRol = ?";
        List<RolesModel> roles = jdbcTemplate.query(sql, rowMapper, id);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    public List<RolesModel> findAll() {
        String sql = "SELECT * FROM Roles";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean update(RolesModel rol) {
        String sql = "UPDATE Roles SET NombreRol = ?, IdUsuario = ? WHERE IdRol = ?";
        int affectedRows = jdbcTemplate.update(sql, rol.getNombreRol(), rol.getIdUsuario(), rol.getIdRol());
        return affectedRows > 0;
    }

    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Roles WHERE IdRol = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    public List<RolesModel> findRolesByIdUsuario(Integer idUsuario) {
        String sql = "SELECT * FROM Roles WHERE IdUsuario = ?";
        return jdbcTemplate.query(sql, rowMapper, idUsuario);
    }

}
