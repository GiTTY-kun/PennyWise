package com.pennywise.service;

import com.pennywise.dao.FeedbackDao;
import com.pennywise.dao.SecurityLogDao;
import com.pennywise.dao.SettingsDao;
import com.pennywise.exception.ValidationException;
import com.pennywise.model.Feedback;
import com.pennywise.model.LogEntry;
import com.pennywise.util.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Admin features: security settings, system settings, security report and user feedback tickets. */
public class SystemService {
    /** Settings that must always exist; the database value wins once the admin changes it. */
    public static final Map<String, String> DEFAULTS = new LinkedHashMap<>();
    public static final List<String> CURRENCIES = List.of("\u20B9", "$", "\u20AC", "\u00A3");

    static {
        DEFAULTS.put("twofa", "true");
        DEFAULTS.put("encrypt", "true");
        DEFAULTS.put("timeout", "15");
        DEFAULTS.put("minpw", "8");
        DEFAULTS.put("sysname", "PennyWise");
        DEFAULTS.put("currency", "\u20B9");
        DEFAULTS.put("maintenance", "false");
    }

    private final SettingsDao settings = new SettingsDao();
    private final SecurityLogDao logs = new SecurityLogDao();
    private final FeedbackDao feedback = new FeedbackDao();

    public void ensureDefaults() {
        DEFAULTS.forEach(settings::putIfAbsent);
    }

    public Map<String, String> all() {
        Map<String, String> m = new LinkedHashMap<>(DEFAULTS);
        m.putAll(settings.asMap());
        return m;
    }

    public int minPasswordLength() { return intSetting("minpw", 8); }
    public int sessionTimeoutMinutes() { return intSetting("timeout", 15); }

    private int intSetting(String key, int fallback) {
        try {
            return Integer.parseInt(all().get(key));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public void saveSecurity(boolean twoFactor, boolean encrypt, String timeout, String minPassword) {
        int t = Validator.requireInt(timeout, "Session timeout", 1, 240);
        int m = Validator.requireInt(minPassword, "Minimum password length", 6, 64);
        settings.put("twofa", String.valueOf(twoFactor));
        settings.put("encrypt", String.valueOf(encrypt));
        settings.put("timeout", String.valueOf(t));
        settings.put("minpw", String.valueOf(m));
        log("Security settings updated");
    }

    public void saveSystem(String name, String currency, boolean maintenance) {
        String n = Validator.requireText(name, "Platform name", 30);
        if (!CURRENCIES.contains(currency)) {
            throw new ValidationException("Choose a valid currency.");
        }
        settings.put("sysname", n);
        settings.put("currency", currency);
        settings.put("maintenance", String.valueOf(maintenance));
        log("System settings updated (maintenance " + (maintenance ? "on" : "off") + ")");
    }

    // ---- security report ----
    public void log(String event) { logs.save(new LogEntry(0, event, null)); }
    public List<LogEntry> securityReport() { return logs.findAll(); }

    // ---- feedback tickets ----
    public void submitFeedback(int userId, String text) {
        feedback.save(new Feedback(0, userId, Validator.requireText(text, "Feedback", 1000), "OPEN", null));
    }
    public List<Feedback> allFeedback() { return feedback.findAll(); }
    public int openFeedbackCount() { return feedback.countOpen(); }
    public void resolveFeedback(int id) { feedback.markResolved(id); }
}
