package com.pennywise.dao;

import com.pennywise.model.Role;
import com.pennywise.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Database operations for the users table. */
public class UserDao extends AbstractJdbcDao<User> {

    @Override protected String table() { return "users"; }

    @Override
    protected User map(ResultSet rs) throws SQLException {
        return User.create(Role.valueOf(rs.getString("role")), rs.getInt("id"), rs.getString("name"),
                rs.getString("email"), rs.getString("password_hash"), rs.getBigDecimal("monthly_income"));
    }

    public Optional<User> findByEmail(String email) {
        return query("SELECT * FROM users WHERE LOWER(email) = LOWER(?)", email).stream().findFirst();
    }

    public List<User> findByRole(Role role) {
        return query("SELECT * FROM users WHERE role = ? ORDER BY name", role.name());
    }

    public int count() {
        return queryWith("SELECT COUNT(*) FROM users", rs -> {
            rs.next();
            return rs.getInt(1);
        });
    }

    @Override
    public User save(User u) {
        if (u.getId() == 0) {
            u.setId(insert("INSERT INTO users(name, email, role, password_hash, monthly_income) VALUES (?,?,?,?,?)",
                    u.getName(), u.getEmail(), u.getRole().name(), u.getPasswordHash(), u.getMonthlyIncome()));
        } else {
            update("UPDATE users SET name = ?, email = ?, role = ?, monthly_income = ? WHERE id = ?",
                    u.getName(), u.getEmail(), u.getRole().name(), u.getMonthlyIncome(), u.getId());
        }
        return u;
    }
}
