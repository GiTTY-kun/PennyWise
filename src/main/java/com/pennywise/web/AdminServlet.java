package com.pennywise.web;

import com.pennywise.exception.ValidationException;
import com.pennywise.model.Role;
import com.pennywise.model.User;
import com.pennywise.service.FinanceService;
import com.pennywise.service.SystemService;
import com.pennywise.service.UserService;
import com.pennywise.util.Bars;
import com.pennywise.util.Validator;
import java.math.BigDecimal;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/** Admin screens: dashboard, user management, financial data security, system settings, feedback. */
@WebServlet("/admin")
public class AdminServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService users = new UserService();
    private final SystemService system = new SystemService();
    private final FinanceService finance = new FinanceService();

    @Override protected String viewName() { return "admin"; }

    @Override
    protected void loadTab(HttpServletRequest req, User me, String tab) {
        switch (tab) {
            case "dashboard":
                Map<String, BigDecimal> spend = finance.platformSpendByCategory();
                req.setAttribute("accountCount", users.all().size());
                req.setAttribute("totalSpend", spend.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
                req.setAttribute("openFeedback", system.openFeedbackCount());
                req.setAttribute("bars", Bars.of(spend));
                break;
            case "users":
                req.setAttribute("users", users.all());
                req.setAttribute("roles", Role.values());
                break;
            case "security":
                req.setAttribute("settings", system.all());
                req.setAttribute("report", system.securityReport());
                break;
            case "system":
                req.setAttribute("settings", system.all());
                req.setAttribute("currencies", SystemService.CURRENCIES);
                break;
            case "feedback":
                req.setAttribute("feedback", system.allFeedback());
                req.setAttribute("names", users.names());
                break;
            default:
                break;
        }
    }

    @Override
    protected String handleAction(HttpServletRequest req, User me, String action) {
        switch (action) {
            case "addUser":
                users.create(param(req, "name"), param(req, "email"), param(req, "password"), param(req, "role"));
                return "User created.";
            case "updateUser":
                users.update(Validator.requireId(param(req, "id")), param(req, "name"), param(req, "role"), me.getId());
                return "User updated.";
            case "deleteUser":
                users.delete(Validator.requireId(param(req, "id")), me.getId());
                return "User deleted.";
            case "saveSecurity":
                system.saveSecurity(req.getParameter("twofa") != null, req.getParameter("encrypt") != null,
                        param(req, "timeout"), param(req, "minpw"));
                return "Security settings updated.";
            case "saveSystem":
                system.saveSystem(param(req, "sysname"), param(req, "currency"), req.getParameter("maintenance") != null);
                AppContext.refresh(getServletContext());
                return "System settings updated.";
            case "resolveFeedback":
                system.resolveFeedback(Validator.requireId(param(req, "id")));
                return "Ticket marked as resolved.";
            default:
                throw new ValidationException("Unknown action.");
        }
    }
}
