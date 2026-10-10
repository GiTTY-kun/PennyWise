package com.pennywise.model;

import java.util.Map;

/**
 * Interface implemented by every user type. Each role decides which dashboard
 * it opens and which menu items it sees (this is where polymorphism is used).
 */
public interface Dashboardable {
    /** URL path of the role's servlet, e.g. "admin". */
    String getDashboardPath();

    /** Menu items: tab key -> label, in display order. */
    Map<String, String> getMenu();
}
