package com.pennywise.dao;

import com.pennywise.model.Message;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database operations for the messages table. */
public class MessageDao extends AbstractJdbcDao<Message> {

    @Override protected String table() { return "messages"; }

    @Override
    protected Message map(ResultSet rs) throws SQLException {
        return new Message(rs.getInt("id"), rs.getInt("user_id"), rs.getString("sender_role"),
                rs.getString("content"), stamp(rs.getTimestamp("created_at")));
    }

    @Override
    public List<Message> findAll() {
        return query("SELECT * FROM messages ORDER BY id DESC");
    }

    public List<Message> findByUser(int userId) {
        return query("SELECT * FROM messages WHERE user_id = ? ORDER BY id DESC", userId);
    }

    @Override
    public Message save(Message m) {
        m.setId(insert("INSERT INTO messages(user_id, sender_role, content) VALUES (?,?,?)",
                m.getUserId(), m.getSenderRole(), m.getContent()));
        return m;
    }
}
