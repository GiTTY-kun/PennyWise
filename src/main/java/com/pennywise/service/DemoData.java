package com.pennywise.service;

import com.pennywise.dao.AdviceDao;
import com.pennywise.dao.BudgetDao;
import com.pennywise.dao.ExpenseDao;
import com.pennywise.dao.FeedbackDao;
import com.pennywise.dao.MessageDao;
import com.pennywise.dao.UserDao;
import com.pennywise.model.Advice;
import com.pennywise.model.Budget;
import com.pennywise.model.Expense;
import com.pennywise.model.Feedback;
import com.pennywise.model.Message;
import com.pennywise.model.Role;
import com.pennywise.model.User;
import com.pennywise.util.PasswordUtil;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Inserts demo accounts and sample data the first time the app starts on an empty database. */
public final class DemoData {
    public static final String DEMO_PASSWORD = "pennywise123";

    private DemoData() { }

    public static void seedIfEmpty() {
        UserDao users = new UserDao();
        if (users.count() > 0) {
            return;
        }
        User admin = users.save(User.create(Role.ADMIN, 0, "Asha Verma", "admin@pennywise.app", PasswordUtil.hash(DEMO_PASSWORD), null));
        User advisor = users.save(User.create(Role.ADVISOR, 0, "Rohan Mehta", "rohan@pennywise.app", PasswordUtil.hash(DEMO_PASSWORD), null));
        User priya = users.save(User.create(Role.USER, 0, "Priya Sharma", "priya@mail.com", PasswordUtil.hash(DEMO_PASSWORD), new BigDecimal("40000")));
        User karan = users.save(User.create(Role.USER, 0, "Karan Singh", "karan@mail.com", PasswordUtil.hash(DEMO_PASSWORD), new BigDecimal("32000")));

        ExpenseDao expenses = new ExpenseDao();
        LocalDate today = LocalDate.now();
        expenses.save(new Expense(0, priya.getId(), "Food", new BigDecimal("4200"), today));
        expenses.save(new Expense(0, priya.getId(), "Rent", new BigDecimal("12000"), today));
        expenses.save(new Expense(0, priya.getId(), "Travel", new BigDecimal("2600"), today));
        expenses.save(new Expense(0, karan.getId(), "Food", new BigDecimal("3100"), today));
        expenses.save(new Expense(0, karan.getId(), "Shopping", new BigDecimal("5400"), today));

        BudgetDao budgets = new BudgetDao();
        budgets.save(new Budget(0, priya.getId(), "Food", new BigDecimal("5000")));
        budgets.save(new Budget(0, priya.getId(), "Rent", new BigDecimal("12000")));
        budgets.save(new Budget(0, priya.getId(), "Travel", new BigDecimal("2000")));
        budgets.save(new Budget(0, karan.getId(), "Food", new BigDecimal("4000")));
        budgets.save(new Budget(0, karan.getId(), "Shopping", new BigDecimal("4000")));

        new AdviceDao().save(new Advice(0, priya.getId(), advisor.getId(),
                "Your travel spend is over budget. Try cutting two cab rides a week and move the saving to an emergency fund.", null));
        new MessageDao().save(new Message(0, priya.getId(), "USER", "Can I start a SIP with 5000 a month?", null));
        new FeedbackDao().save(new Feedback(0, karan.getId(), "Please add a CSV export for reports.", "OPEN", null));

        SystemService system = new SystemService();
        system.ensureDefaults();
        system.log("Demo data created by " + admin.getName());
    }
}
