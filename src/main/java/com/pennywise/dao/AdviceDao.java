package com.pennywise.dao;

import com.pennywise.model.Advice;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database operations for the advice table. */
public class AdviceDao extends AbstractJdbcDao<Advice> {

    @Override protected String table() { return "advice"; }

    @Override
    protected Advice map(ResultSet rs) throws SQLException {
        return new Advice(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("advisor_id"),
                rs.getString("content"), stamp(rs.getTimestamp("created_at")));
    }

    @Override
    public List<Advice> findAll() {
        return query("SELECT * FROM advice ORDER BY id DESC");
    }

    public List<Advice> findByUser(int userId) {
        return query("SELECT * FROM advice WHERE user_id = ? ORDER BY id DESC", userId);
    }

    @Override
    public Advice save(Advice a) {
        a.setId(insert("INSERT INTO advice(user_id, advisor_id, content) VALUES (?,?,?)",
                a.getUserId(), a.getAdvisorId(), a.getContent()));
        return a;
    }
}
