package com.pennywise.util;

import com.pennywise.exception.ValidationException;
import com.pennywise.model.Role;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Input checks. Every method throws ValidationException with a friendly message when the input is bad. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999");

    private Validator() { }

    public static String requireText(String value, String field, int maxLength) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        if (v.length() > maxLength) {
            throw new ValidationException(field + " must be at most " + maxLength + " characters.");
        }
        return v;
    }

    public static String requireEmail(String value) {
        String v = requireText(value, "Email", 120);
        if (!EMAIL.matcher(v).matches()) {
            throw new ValidationException("Enter a valid email address.");
        }
        return v;
    }

    public static void requirePassword(String password, int minLength) {
        if (password == null || password.length() < minLength) {
            throw new ValidationException("Password needs at least " + minLength + " characters.");
        }
    }

    /** A money amount above zero, rounded to 2 decimals. */
    public static BigDecimal requireAmount(String value, String field) {
        BigDecimal a = parseDecimal(value, field);
        if (a.signum() <= 0) {
            throw new ValidationException(field + " must be above 0.");
        }
        return a;
    }

    /** Like requireAmount but blank or 0 is allowed (used for monthly income). */
    public static BigDecimal optionalAmount(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal a = parseDecimal(value, field);
        if (a.signum() < 0) {
            throw new ValidationException(field + " cannot be negative.");
        }
        return a;
    }

    private static BigDecimal parseDecimal(String value, String field) {
        try {
            BigDecimal a = new BigDecimal(value == null ? "" : value.trim()).setScale(2, RoundingMode.HALF_UP);
            if (a.compareTo(MAX_AMOUNT) > 0) {
                throw new ValidationException(field + " is too large.");
            }
            return a;
        } catch (NumberFormatException e) {
            throw new ValidationException(field + " must be a number.");
        }
    }

    public static int requireInt(String value, String field, int min, int max) {
        try {
            int n = Integer.parseInt(value == null ? "" : value.trim());
            if (n < min || n > max) {
                throw new ValidationException(field + " must be between " + min + " and " + max + ".");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new ValidationException(field + " must be a whole number.");
        }
    }

    public static int requireId(String value) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid request.");
        }
    }

    /** Blank means today. */
    public static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Enter a valid date.");
        }
    }

    public static String requireCategory(String value) {
        if (value == null || !Categories.ALL.contains(value)) {
            throw new ValidationException("Choose a valid category.");
        }
        return value;
    }

    public static Role parseRole(String value) {
        try {
            return Role.valueOf(value == null ? "" : value.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Choose a valid role.");
        }
    }
}
