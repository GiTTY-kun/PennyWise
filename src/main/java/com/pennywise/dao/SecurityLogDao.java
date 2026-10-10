package com.pennywise.dao;

import com.pennywise.model.LogEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database operations for the security_log table (security report). */
public class SecurityLogDao extends AbstractJdbcDao<LogEntry> {

    @Override protected String table() { return "security_log"; }

    @Override
    protected LogEntry map(ResultSet rs) throws SQLException {
        return new LogEntry(rs.getInt("id"), rs.getString("event_text"), stamp(rs.getTimestamp("created_at")));
    }

    /** Latest 50 events first. */
    @Override
    public List<LogEntry> findAll() {
        return query("SELECT * FROM security_log ORDER BY id DESC LIMIT 50");
    }

    @Override
    public LogEntry save(LogEntry e) {
        e.setId(insert("INSERT INTO security_log(event_text) VALUES (?)", e.getEvent()));
        return e;
    }
}
