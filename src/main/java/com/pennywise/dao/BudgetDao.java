package com.pennywise.dao;

import com.pennywise.model.Budget;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database operations for the budgets table (one budget per user and category). */
public class BudgetDao extends AbstractJdbcDao<Budget> {

    @Override protected String table() { return "budgets"; }

    @Override
    protected Budget map(ResultSet rs) throws SQLException {
        return new Budget(rs.getInt("id"), rs.getInt("user_id"), rs.getString("category"), rs.getBigDecimal("amount"));
    }

    public List<Budget> findByUser(int userId) {
        return query("SELECT * FROM budgets WHERE user_id = ? ORDER BY category", userId);
    }

    /** Insert, or update the amount when this user already has a budget for the category. */
    @Override
    public Budget save(Budget b) {
        int id = insert("INSERT INTO budgets(user_id, category, amount) VALUES (?,?,?) "
                        + "ON DUPLICATE KEY UPDATE amount = ?",
                b.getUserId(), b.getCategory(), b.getAmount(), b.getAmount());
        if (id != 0) {
            b.setId(id);
        }
        return b;
    }
}
