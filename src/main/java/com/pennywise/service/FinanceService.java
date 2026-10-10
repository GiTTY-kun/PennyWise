package com.pennywise.service;

import com.pennywise.dao.BudgetDao;
import com.pennywise.dao.ExpenseDao;
import com.pennywise.model.Budget;
import com.pennywise.model.BudgetStatus;
import com.pennywise.model.Expense;
import com.pennywise.model.FinancialSummary;
import com.pennywise.model.User;
import com.pennywise.util.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Expense tracking, budgets, summaries and reports for a user. "Spent" always means the current month. */
public class FinanceService {
    private final ExpenseDao expenses = new ExpenseDao();
    private final BudgetDao budgets = new BudgetDao();

    public void addExpense(int userId, String category, String amount, String date) {
        expenses.save(new Expense(0, userId, Validator.requireCategory(category),
                Validator.requireAmount(amount, "Amount"), Validator.parseDate(date)));
        BudgetMonitor.getInstance().checkNow();
    }

    public void deleteExpense(int expenseId, int userId) {
        expenses.deleteOwned(expenseId, userId);
        BudgetMonitor.getInstance().checkNow();
    }

    public List<Expense> expenses(int userId) { return expenses.findByUser(userId); }

    public void setBudget(int userId, String category, String amount) {
        budgets.save(new Budget(0, userId, Validator.requireCategory(category),
                Validator.requireAmount(amount, "Budget amount")));
        BudgetMonitor.getInstance().checkNow();
    }

    public Map<String, BigDecimal> monthSpendByCategory(int userId) {
        return expenses.sumByCategory(userId, YearMonth.now());
    }

    public Map<String, BigDecimal> platformSpendByCategory() {
        return expenses.sumByCategoryAllUsers();
    }

    public List<BudgetStatus> budgetStatus(int userId) {
        Map<String, BigDecimal> spent = monthSpendByCategory(userId);
        return budgets.findByUser(userId).stream()
                .map(b -> BudgetStatus.of(b.getCategory(), spent.getOrDefault(b.getCategory(), BigDecimal.ZERO), b.getAmount()))
                .collect(Collectors.toList());
    }

    public FinancialSummary summary(User user) {
        BigDecimal spent = monthSpendByCategory(user.getId()).values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal income = user.getMonthlyIncome();
        BigDecimal left = income.subtract(spent);
        int rate = income.signum() > 0
                ? left.multiply(BigDecimal.valueOf(100)).divide(income, 0, RoundingMode.HALF_UP).intValue()
                : 0;
        return new FinancialSummary(income, spent, left, rate);
    }

    /** Simple rule-based tips built from the user's own numbers. */
    public List<String> tips(User user) {
        List<String> tips = new ArrayList<>();
        FinancialSummary s = summary(user);
        for (BudgetStatus b : budgetStatus(user.getId())) {
            if (b.isOver()) {
                tips.add(b.getCategory() + " spending is over its budget this month. Try to pause non-essential "
                        + b.getCategory().toLowerCase() + " spending.");
            }
        }
        if (s.getIncome().signum() == 0) {
            tips.add("Add your monthly income in Profile so savings can be calculated.");
        } else if (s.getSavingsRate() < 20) {
            tips.add("You are saving " + s.getSavingsRate() + "% of your income this month. A common target is 20%.");
        }
        if (tips.isEmpty()) {
            tips.add("You are within budget and saving well. Consider moving some surplus into savings.");
        }
        return tips;
    }

    public String expensesCsv(int userId) {
        StringBuilder sb = new StringBuilder("Date,Category,Amount\n");
        for (Expense e : expenses.findByUser(userId)) {
            sb.append(e.getDate()).append(',').append(e.getCategory()).append(',').append(e.getAmount()).append('\n');
        }
        return sb.toString();
    }
}
