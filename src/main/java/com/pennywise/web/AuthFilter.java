package com.pennywise.web;

import com.pennywise.model.User;
import com.pennywise.service.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Runs before /admin, /advisor and /user. It:
 *  1. checks that someone is logged in (and still exists in the database),
 *  2. checks the person is allowed on this URL (an Admin cannot open /advisor, and so on),
 *  3. shows the maintenance page to non-admins when maintenance mode is on,
 *  4. rejects POST requests that do not carry the session's CSRF token.
 */
@WebFilter(urlPatterns = {"/admin", "/advisor", "/user"})
public class AuthFilter implements Filter {
    private final UserService users = new UserService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String ctx = req.getContextPath();

        HttpSession session = req.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute("userId");
        Optional<User> found = userId == null ? Optional.empty() : users.find(userId);
        if (!found.isPresent()) {
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(ctx + "/login");
            return;
        }
        User me = found.get();

        // Role check: the URL path must match the user's own dashboard path.
        if (!req.getServletPath().equals("/" + me.getDashboardPath())) {
            resp.sendRedirect(ctx + "/" + me.getDashboardPath());
            return;
        }

        if (Boolean.TRUE.equals(req.getServletContext().getAttribute("maintenance"))
                && !"admin".equals(me.getDashboardPath())) {
            req.setAttribute("me", me);
            req.getRequestDispatcher("/WEB-INF/views/maintenance.jsp").forward(req, resp);
            return;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String expected = (String) session.getAttribute("csrf");
            String actual = req.getParameter("_csrf");
            if (expected == null || actual == null || !MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing security token");
                return;
            }
        }

        req.setAttribute("me", me);
        chain.doFilter(request, response);
    }
}
