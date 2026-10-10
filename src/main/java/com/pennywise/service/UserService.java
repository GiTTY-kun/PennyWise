package com.pennywise.service;

import com.pennywise.dao.UserDao;
import com.pennywise.exception.AuthenticationException;
import com.pennywise.exception.ValidationException;
import com.pennywise.model.Role;
import com.pennywise.model.User;
import com.pennywise.util.PasswordUtil;
import com.pennywise.util.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Login and user-account management. */
public class UserService {
    private final UserDao users = new UserDao();
    private final SystemService system = new SystemService();

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Enter your email and password.");
        }
        User u = users.findByEmail(email.trim())
                .orElseThrow(() -> new AuthenticationException("Wrong email or password."));
        if (!PasswordUtil.verify(password, u.getPasswordHash())) {
            throw new AuthenticationException("Wrong email or password.");
        }
        return u;
    }

    public Optional<User> find(int id) { return users.findById(id); }
    public List<User> all() { return users.findAll(); }
    public List<User> clients() { return users.findByRole(Role.USER); }

    /** id -> name lookup used by pages that list records from many users. */
    public Map<Integer, String> names() {
        Map<Integer, String> names = new LinkedHashMap<>();
        for (User u : users.findAll()) {
            names.put(u.getId(), u.getName());
        }
        return names;
    }

    public User create(String name, String email, String password, String roleText) {
        String n = Validator.requireText(name, "Name", 80);
        String e = Validator.requireEmail(email);
        Validator.requirePassword(password, system.minPasswordLength());
        Role role = Validator.parseRole(roleText);
        if (users.findByEmail(e).isPresent()) {
            throw new ValidationException("That email is already used.");
        }
        User u = users.save(User.create(role, 0, n, e, PasswordUtil.hash(password), null));
        system.log("User created: " + n + " (" + role.getLabel() + ")");
        return u;
    }

    /** Changes name and role. A role is a class, so a new object of the right subclass is built. */
    public void update(int id, String name, String roleText, int actingUserId) {
        User old = users.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        String n = Validator.requireText(name, "Name", 80);
        Role role = Validator.parseRole(roleText);
        if (id == actingUserId && role != old.getRole()) {
            throw new ValidationException("You cannot change your own role.");
        }
        users.save(User.create(role, id, n, old.getEmail(), old.getPasswordHash(), old.getMonthlyIncome()));
        if (role != old.getRole()) {
            system.log("Role changed for " + n + ": " + old.getRole().getLabel() + " -> " + role.getLabel());
        }
    }

    public void delete(int id, int actingUserId) {
        if (id == actingUserId) {
            throw new ValidationException("You cannot delete your own account.");
        }
        User u = users.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        users.delete(id);       // expenses, budgets, ... are removed by ON DELETE CASCADE
        system.log("User deleted: " + u.getName());
    }

    public void updateProfile(int id, String name, String email, String income) {
        User u = users.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        String e = Validator.requireEmail(email);
        users.findByEmail(e).filter(other -> other.getId() != id).ifPresent(other -> {
            throw new ValidationException("That email is already used.");
        });
        u.setName(Validator.requireText(name, "Name", 80));
        u.setEmail(e);
        u.setMonthlyIncome(Validator.optionalAmount(income, "Monthly income"));
        users.save(u);
    }
}
