package com.pennywise.web;

import com.pennywise.exception.ValidationException;
import com.pennywise.model.BudgetStatus;
import com.pennywise.model.FinancialSummary;
import com.pennywise.model.User;
import com.pennywise.service.AdvisoryService;
import com.pennywise.service.FinanceService;
import com.pennywise.service.UserService;
import com.pennywise.util.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/** Advisor screens: dashboard, give advice, talk to users, track progress, advice history. */
@WebServlet("/advisor")
public class AdvisorServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService users = new UserService();
    private final FinanceService finance = new FinanceService();
    private final AdvisoryService advisory = new AdvisoryService();

    @Override protected String viewName() { return "advisor"; }

    @Override
    protected void loadTab(HttpServletRequest req, User me, String tab) {
        List<User> clients = users.clients();
        Map<Integer, String> names = users.names();
        req.setAttribute("clients", clients);
        req.setAttribute("names", names);
        switch (tab) {
            case "dashboard":
                req.setAttribute("clientCount", clients.size());
                req.setAttribute("adviceCount", advisory.allAdvice().size());
                req.setAttribute("messageCount", advisory.allMessages().size());
                req.setAttribute("stats", advisory.messageStats(names));
                break;
            case "advice":
                Map<Integer, FinancialSummary> summaries = new LinkedHashMap<>();
                Map<Integer, List<String>> tips = new LinkedHashMap<>();
                for (User c : clients) {
                    summaries.put(c.getId(), finance.summary(c));
                    tips.put(c.getId(), finance.tips(c));
                }
                req.setAttribute("summaries", summaries);
                req.setAttribute("tips", tips);
                break;
            case "interaction":
                req.setAttribute("messages", advisory.allMessages());
                break;
            case "progress":
                Map<Integer, List<BudgetStatus>> progress = new LinkedHashMap<>();
                for (User c : clients) {
                    progress.put(c.getId(), finance.budgetStatus(c.getId()));
                }
                req.setAttribute("progress", progress);
                break;
            case "history":
                req.setAttribute("history", advisory.allAdvice());
                break;
            default:
                break;
        }
    }

    @Override
    protected String handleAction(HttpServletRequest req, User me, String action) {
        int clientId = Validator.requireId(param(req, "userId"));
        switch (action) {
            case "sendAdvice":
                advisory.giveAdvice(me.getId(), clientId, param(req, "content"));
                return "Advice sent.";
            case "sendMessage":
                advisory.sendMessage("ADVISOR", clientId, param(req, "content"));
                return "Message sent.";
            default:
                throw new ValidationException("Unknown action.");
        }
    }
}
