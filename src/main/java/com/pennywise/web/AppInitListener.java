package com.pennywise.web;

import com.pennywise.service.BudgetMonitor;
import com.pennywise.service.DemoData;
import com.pennywise.service.SystemService;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Runs when Tomcat starts / stops the app: creates defaults and demo data, starts the budget thread. */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();
        try {
            new SystemService().ensureDefaults();
            DemoData.seedIfEmpty();
            AppContext.refresh(ctx);
            BudgetMonitor.getInstance().start();
            ctx.log("PennyWise started");
        } catch (RuntimeException | ExceptionInInitializerError e) {
            ctx.log("PennyWise could not reach the database. Check db.properties and that MySQL is running.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        BudgetMonitor.getInstance().stop();
    }
}
