package com.pennywise.web;

import com.pennywise.exception.AppException;
import com.pennywise.model.User;
import com.pennywise.service.SystemService;
import com.pennywise.service.UserService;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Shows the login form and signs the user in. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService users = new UserService();
    private final SystemService system = new SystemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardToForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            User user = users.login(email, req.getParameter("password"));

            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();                      // new session id after login (prevents session fixation)
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("csrf", UUID.randomUUID().toString());
            session.setMaxInactiveInterval(system.sessionTimeoutMinutes() * 60);
            resp.sendRedirect(req.getContextPath() + "/" + user.getDashboardPath());
        } catch (AppException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            forwardToForm(req, resp);
        }
    }

    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
