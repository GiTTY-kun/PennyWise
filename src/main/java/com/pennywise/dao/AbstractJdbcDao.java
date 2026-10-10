package com.pennywise.dao;

import com.pennywise.exception.DataAccessException;
import com.pennywise.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shared JDBC code. Subclasses only say which table they use, how a row becomes an object (map)
 * and how to save one. All SQL uses PreparedStatement (no string concatenation of user input),
 * and every connection is closed with try-with-resources.
 */
public abstract class AbstractJdbcDao<T> implements Dao<T, Integer> {

    /** Turns a whole ResultSet into some result R (lambda friendly). */
    @FunctionalInterface
    public interface ResultReader<R> {
        R read(ResultSet rs) throws SQLException;
    }

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    protected abstract String table();
    protected abstract T map(ResultSet row) throws SQLException;

    @Override
    public Optional<T> findById(Integer id) {
        return query("SELECT * FROM " + table() + " WHERE id = ?", id).stream().findFirst();
    }

    @Override
    public List<T> findAll() {
        return query("SELECT * FROM " + table() + " ORDER BY id");
    }

    @Override
    public void delete(Integer id) {
        update("DELETE FROM " + table() + " WHERE id = ?", id);
    }

    /** Runs a SELECT and lets the reader build the result. */
    protected <R> R queryWith(String sql, ResultReader<R> reader, Object... params) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return reader.read(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage(), e);
        }
    }

    /** Runs a SELECT and maps every row with map(). */
    protected List<T> query(String sql, Object... params) {
        return queryWith(sql, rs -> {
            List<T> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(map(rs));
            }
            return rows;
        }, params);
    }

    /** INSERT / UPDATE / DELETE. Returns the number of rows changed. */
    protected int update(String sql, Object... params) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage(), e);
        }
    }

    /** INSERT that returns the new auto-generated id. */
    protected int insert(String sql, Object... params) {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage(), e);
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    protected static String stamp(Timestamp t) {
        return t == null ? "" : t.toLocalDateTime().format(STAMP);
    }
}
