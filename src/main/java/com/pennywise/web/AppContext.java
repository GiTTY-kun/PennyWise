package com.pennywise.web;

import com.pennywise.service.SystemService;
import java.util.Map;
import javax.servlet.ServletContext;

/** Copies the settings every page needs (name, currency, maintenance flag) into the ServletContext. */
public final class AppContext {
    private AppContext() { }

    public static void refresh(ServletContext ctx) {
        Map<String, String> s = new SystemService().all();
        ctx.setAttribute("sysName", s.get("sysname"));
        ctx.setAttribute("currency", s.get("currency"));
        ctx.setAttribute("maintenance", Boolean.valueOf(s.get("maintenance")));
    }
}
