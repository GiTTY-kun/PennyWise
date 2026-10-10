package com.pennywise.web;

import com.pennywise.exception.ValidationException;
import com.pennywise.model.FinancialSummary;
import com.pennywise.model.User;
import com.pennywise.service.AdvisoryService;
import com.pennywise.service.BudgetMonitor;
import com.pennywise.service.FinanceService;
import com.pennywise.service.SystemService;
import com.pennywise.service.UserService;
import com.pennywise.util.Bars;
import com.pennywise.util.Categories;
import com.pennywise.util.Validator;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** User screens: dashboard, expenses, budgets, advice, reports, profile, feedback. */
@WebServlet("/user")
public class UserServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService users = new UserService();
    private final FinanceService finance = new FinanceService();
    private final AdvisoryService advisory = new AdvisoryService();
    private final SystemService system = new SystemService();

    @Override protected String viewName() { return "user"; }

    @Override
    protected void loadTab(HttpServletRequest req, User me, String tab) {
        int id = me.getId();
        req.setAttribute("categories", Categories.ALL);
        req.setAttribute("today", LocalDate.now().toString());
        switch (tab) {
            case "dashboard":
                req.setAttribute("summary", finance.summary(me));
                req.setAttribute("budgets", finance.budgetStatus(id));
                req.setAttribute("alerts", BudgetMonitor.getInstance().alertsFor(id));   // written by the background thread
                break;
            case "expenses":
                req.setAttribute("expenses", finance.expenses(id));
                req.setAttribute("bars", Bars.of(finance.monthSpendByCategory(id)));
                break;
            case "budgets":
                req.setAttribute("budgets", finance.budgetStatus(id));
                break;
            case "advice":
                req.setAttribute("adviceList", advisory.adviceFor(id));
                req.setAttribute("messages", advisory.messagesFor(id));
                req.setAttribute("tips", finance.tips(me));
                break;
            case "reports":
                FinancialSummary s = finance.summary(me);
                req.setAttribute("summary", s);
                req.setAttribute("bars", Bars.of(finance.monthSpendByCategory(id)));
                break;
            default:
                break;   // profile and feedback only need the logged-in user ("me")
        }
    }

    @Override
    protected boolean handleDownload(HttpServletRequest req, HttpServletResponse resp, User me, String tab)
            throws IOException {
        if ("reports".equals(tab) && "csv".equals(req.getParameter("export"))) {
            resp.setContentType("text/csv");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"pennywise-expenses.csv\"");
            resp.getWriter().write(finance.expensesCsv(me.getId()));
            return true;
        }
        return false;
    }

    @Override
    protected String handleAction(HttpServletRequest req, User me, String action) {
        switch (action) {
            case "addExpense":
                finance.addExpense(me.getId(), param(req, "category"), param(req, "amount"), param(req, "date"));
                return "Expense added.";
            case "deleteExpense":
                finance.deleteExpense(Validator.requireId(param(req, "id")), me.getId());
                return "Expense deleted.";
            case "setBudget":
                finance.setBudget(me.getId(), param(req, "category"), param(req, "amount"));
                return "Budget saved.";
            case "askAdvice":
                advisory.sendMessage("USER", me.getId(), param(req, "content"));
                return "Your request was sent to the advisor.";
            case "saveProfile":
                users.updateProfile(me.getId(), param(req, "name"), param(req, "email"), param(req, "income"));
                return "Profile saved.";
            case "sendFeedback":
                system.submitFeedback(me.getId(), param(req, "content"));
                return "Thank you. Your feedback was sent to the admin.";
            default:
                throw new ValidationException("Unknown action.");
        }
    }
}
