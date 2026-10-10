package com.pennywise.dao;

import com.pennywise.model.Feedback;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database operations for the feedback (support ticket) table. */
public class FeedbackDao extends AbstractJdbcDao<Feedback> {

    @Override protected String table() { return "feedback"; }

    @Override
    protected Feedback map(ResultSet rs) throws SQLException {
        return new Feedback(rs.getInt("id"), rs.getInt("user_id"), rs.getString("content"),
                rs.getString("status"), stamp(rs.getTimestamp("created_at")));
    }

    @Override
    public List<Feedback> findAll() {
        return query("SELECT * FROM feedback ORDER BY id DESC");
    }

    public int countOpen() {
        return queryWith("SELECT COUNT(*) FROM feedback WHERE status = 'OPEN'", rs -> {
            rs.next();
            return rs.getInt(1);
        });
    }

    public void markResolved(int id) {
        update("UPDATE feedback SET status = 'RESOLVED' WHERE id = ?", id);
    }

    @Override
    public Feedback save(Feedback f) {
        f.setId(insert("INSERT INTO feedback(user_id, content) VALUES (?,?)", f.getUserId(), f.getContent()));
        return f;
    }
}
