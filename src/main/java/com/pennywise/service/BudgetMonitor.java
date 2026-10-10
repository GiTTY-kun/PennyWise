package com.pennywise.service;

import com.pennywise.dao.BudgetDao;
import com.pennywise.dao.ExpenseDao;
import com.pennywise.model.Budget;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Background thread that checks every user's budgets and keeps a list of "over budget" alerts.
 *
 * Multithreading: a single-thread ScheduledExecutorService runs this task every 30 seconds
 * (and on demand through checkNow()). Web requests read the alerts while the monitor thread writes
 * them, so the shared map is only touched inside synchronized methods.
 */
public final class BudgetMonitor implements Runnable {
    private static final BudgetMonitor INSTANCE = new BudgetMonitor();

    private final BudgetDao budgets = new BudgetDao();
    private final ExpenseDao expenses = new ExpenseDao();
    private final Map<Integer, List<String>> alerts = new HashMap<>();   // guarded by "this"
    private final AtomicInteger runs = new AtomicInteger();
    private ScheduledExecutorService pool;                               // guarded by "this"

    private BudgetMonitor() { }

    public static BudgetMonitor getInstance() { return INSTANCE; }

    public synchronized void start() {
        if (pool != null) {
            return;
        }
        pool = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "budget-monitor");
            t.setDaemon(true);
            return t;
        });
        pool.scheduleWithFixedDelay(this, 0, 30, TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
    }

    /** Asks the background thread to re-check now (called after an expense or budget changes). */
    public synchronized void checkNow() {
        if (pool != null) {
            pool.execute(this);
        }
    }

    /** Runs on the monitor thread. */
    @Override
    public void run() {
        try {
            YearMonth month = YearMonth.now();
            Map<Integer, List<Budget>> byUser = budgets.findAll().stream()
                    .collect(Collectors.groupingBy(Budget::getUserId));
            Map<Integer, List<String>> fresh = new HashMap<>();
            for (Map.Entry<Integer, List<Budget>> entry : byUser.entrySet()) {
                Map<String, BigDecimal> spent = expenses.sumByCategory(entry.getKey(), month);
                for (Budget b : entry.getValue()) {
                    BigDecimal s = spent.getOrDefault(b.getCategory(), BigDecimal.ZERO);
                    if (s.compareTo(b.getAmount()) > 0) {
                        fresh.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                                .add(b.getCategory() + " is over budget: spent " + s + " of " + b.getAmount());
                    }
                }
            }
            replaceAlerts(fresh);
            runs.incrementAndGet();
        } catch (RuntimeException e) {
            // Never let an exception escape: it would cancel all future scheduled runs.
            System.err.println("[budget-monitor] check failed: " + e.getMessage());
        }
    }

    private synchronized void replaceAlerts(Map<Integer, List<String>> fresh) {
        alerts.clear();
        alerts.putAll(fresh);
    }

    /** Safe to call from any request thread; returns a copy. */
    public synchronized List<String> alertsFor(int userId) {
        return new ArrayList<>(alerts.getOrDefault(userId, new ArrayList<>()));
    }

    public int getRunCount() { return runs.get(); }
}
