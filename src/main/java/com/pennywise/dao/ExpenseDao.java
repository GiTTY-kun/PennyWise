package com.pennywise.dao;

import com.pennywise.model.Expense;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Database operations for the expenses table. */
public class ExpenseDao extends AbstractJdbcDao<Expense> {

    @Override protected String table() { return "expenses"; }

    @Override
    protected Expense map(ResultSet rs) throws SQLException {
        return new Expense(rs.getInt("id"), rs.getInt("user_id"), rs.getString("category"),
                rs.getBigDecimal("amount"), rs.getDate("expense_date").toLocalDate());
    }

    public List<Expense> findByUser(int userId) {
        return query("SELECT * FROM expenses WHERE user_id = ? ORDER BY expense_date DESC, id DESC", userId);
    }

    @Override
    public Expense save(Expense e) {
        e.setId(insert("INSERT INTO expenses(user_id, category, amount, expense_date) VALUES (?,?,?,?)",
                e.getUserId(), e.getCategory(), e.getAmount(), e.getDate()));
        return e;
    }

    /** Deletes only if the expense belongs to the given user (so one user cannot delete another's data). */
    public boolean deleteOwned(int id, int userId) {
        return update("DELETE FROM expenses WHERE id = ? AND user_id = ?", id, userId) > 0;
    }

    /** Total per category for one user in one month. */
    public Map<String, BigDecimal> sumByCategory(int userId, YearMonth month) {
        return queryWith("SELECT category, SUM(amount) FROM expenses "
                        + "WHERE user_id = ? AND expense_date >= ? AND expense_date < ? "
                        + "GROUP BY category ORDER BY category",
                ExpenseDao::readTotals, userId, month.atDay(1), month.plusMonths(1).atDay(1));
    }

    /** Total per category across all users (admin overview). */
    public Map<String, BigDecimal> sumByCategoryAllUsers() {
        return queryWith("SELECT category, SUM(amount) FROM expenses GROUP BY category ORDER BY category",
                ExpenseDao::readTotals);
    }

    private static Map<String, BigDecimal> readTotals(ResultSet rs) throws SQLException {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        while (rs.next()) {
            totals.put(rs.getString(1), rs.getBigDecimal(2));
        }
        return totals;
    }
}
