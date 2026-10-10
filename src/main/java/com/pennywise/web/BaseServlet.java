package com.pennywise.web;

import com.pennywise.exception.AppException;
import com.pennywise.model.User;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Template for the three role servlets (Template Method pattern).
 *
 *  GET  /role?tab=x  -> loadTab() puts the data for that tab into the request, then the JSP view is shown.
 *  POST /role        -> handleAction() does the change, then we REDIRECT back to the tab
 *                       (Post/Redirect/Get, so refreshing the page never repeats the action).
 *
 * The result of an action is kept in the session as a one-time "flash" message.
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Name of the JSP in WEB-INF/views (without .jsp). */
    protected abstract String viewName();

    /** Loads the data needed by one tab into request attributes. */
    protected abstract void loadTab(HttpServletRequest req, User me, String tab);

    /** Performs a form action and returns the confirmation message. Throws AppException on bad input. */
    protected abstract String handleAction(HttpServletRequest req, User me, String action);

    /** Subclasses can serve a file download instead of a page. Return true when the response is done. */
    protected boolean handleDownload(HttpServletRequest req, HttpServletResponse resp, User me, String tab)
            throws IOException {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = (User) req.getAttribute("me");
        Map<String, String> menu = me.getMenu();
        String tab = menu.containsKey(req.getParameter("tab")) ? req.getParameter("tab") : "dashboard";

        if (handleDownload(req, resp, me, tab)) {
            return;
        }
        req.setAttribute("tab", tab);
        req.setAttribute("menu", menu);
        req.setAttribute("basePath", me.getDashboardPath());
        showFlash(req);
        try {
            loadTab(req, me, tab);
        } catch (AppException e) {
            req.setAttribute("err", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/" + viewName() + ".jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User me = (User) req.getAttribute("me");
        String tab = menuTab(req, me);
        HttpSession session = req.getSession();
        try {
            session.setAttribute("flashMsg", handleAction(req, me, param(req, "action")));
        } catch (AppException e) {
            session.setAttribute("flashErr", e.getMessage());
        } catch (RuntimeException e) {
            log("Unexpected error in " + getClass().getSimpleName(), e);
            session.setAttribute("flashErr", "Something went wrong. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/" + me.getDashboardPath() + "?tab=" + tab);
    }

    private static String menuTab(HttpServletRequest req, User me) {
        String tab = req.getParameter("tab");
        return tab != null && me.getMenu().containsKey(tab) ? tab : "dashboard";
    }

    private static void showFlash(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) {
            return;
        }
        req.setAttribute("msg", s.getAttribute("flashMsg"));
        req.setAttribute("err", s.getAttribute("flashErr"));
        s.removeAttribute("flashMsg");
        s.removeAttribute("flashErr");
    }

    /** Request parameter, never null. */
    protected static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v;
    }
}
