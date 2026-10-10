package com.pennywise.util;

import java.util.List;

/** The fixed list of expense / budget categories. */
public final class Categories {
    public static final List<String> ALL = List.of("Food", "Rent", "Travel", "Shopping", "Bills", "Health", "Other");
    private Categories() { }
}
