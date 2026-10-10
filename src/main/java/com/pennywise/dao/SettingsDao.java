package com.pennywise.dao;

import com.pennywise.model.Setting;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Database operations for the settings table (security + system configuration). */
public class SettingsDao extends AbstractJdbcDao<Setting> {

    @Override protected String table() { return "settings"; }

    @Override
    protected Setting map(ResultSet rs) throws SQLException {
        return new Setting(rs.getInt("id"), rs.getString("skey"), rs.getString("svalue"));
    }

    public Map<String, String> asMap() {
        Map<String, String> m = new LinkedHashMap<>();
        for (Setting s : findAll()) {
            m.put(s.getKey(), s.getValue());
        }
        return m;
    }

    /** Insert or update a setting. */
    @Override
    public Setting save(Setting s) {
        update("INSERT INTO settings(skey, svalue) VALUES (?,?) ON DUPLICATE KEY UPDATE svalue = ?",
                s.getKey(), s.getValue(), s.getValue());
        return s;
    }

    public void put(String key, String value) {
        save(new Setting(0, key, value));
    }

    /** Only inserts when the key does not exist yet (used to create defaults). */
    public void putIfAbsent(String key, String value) {
        update("INSERT IGNORE INTO settings(skey, svalue) VALUES (?,?)", key, value);
    }
}
