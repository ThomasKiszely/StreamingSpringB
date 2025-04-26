package com.example.streamingspringb.Infrastructure;

import com.example.streamingspringb.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User checkUser(String email) {
        String sql = "select iduser, username, email, password, birthdate, role from user\n" +
                "join user_roles on fkuser = iduser\n" +
                "join roles on idroles = fkrole WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs,  rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("iduser"));
            user.setName(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setBirthdate(rs.getDate("birthdate").toLocalDate());
            user.setRole(rs.getString("role"));
            return user;
        });
    }
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, birthdate = ? WHERE iduser = ?";
        int updated = jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate(), user.getId());
        if (updated > 0) {
            return true;
        }
        return false;
    }
    public boolean updateUserNoPass(User user) {
        String sql = "UPDATE user SET username = ?, email = ?, birthdate = ? WHERE iduser = ?";
        int updated = jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getBirthdate(), user.getId());
        if (updated > 0) {
            return true;
        }
        return false;
    }
    public User registerUser(User user) {
        String sql = "INSERT INTO user(username, email, password, birthdate) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getBirthdate().toString());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        String roleSql = "INSERT INTO user_roles(fkuser, fkrole) VALUES(?, 2)";
        jdbcTemplate.update(roleSql, user.getId());
        user.setRole("ROLE_USER");
        return user;
    }
    public List<User> getAllUsers() {
        String sql = "select iduser, username, email, password, birthdate, role from user\n" +
                "join user_roles on fkuser = iduser\n" +
                "join roles on idroles = fkrole;";
                return jdbcTemplate.query(sql, ((rs, rowNum) -> new User(rs.getInt("iduser"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getDate("birthdate").toLocalDate(), rs.getString("role"))));
    }
    public List<User> getUserPages(int page, int size) {
        String sql = "select iduser, username, email, password, birthdate, role from user\n" +
                "join user_roles on fkuser = iduser\n" +
                "join roles on idroles = fkrole\n" +
                "limit ? offset ?;";
        int offset = (page - 1) * size;
        return jdbcTemplate.query(sql, new Object[]{size, offset}, ((rs, rowNum) -> new User(rs.getInt("iduser"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getDate("birthdate").toLocalDate(), rs.getString("role"))));
    }

    public boolean deleteUser(int id) {
        String sql = "delete from user where iduser = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted > 0) {
            return true;
        }
        return false;
    }
    public boolean giveAdminRights(int id) {
        String sql = "UPDATE user_roles set fkrole = 1 where fkuser = ?";
        int updated = jdbcTemplate.update(sql, id);
        if (updated > 0) {
            return true;
        }
        return false;
    }
    public boolean removeAdminRights(int id) {
        String sql = "UPDATE user_roles SET fkrole = 2 WHERE fkuser = ?";
        int updated = jdbcTemplate.update(sql, id);
        if (updated > 0) {
            return true;
        }
        return false;
    }
}
