package com.pennywise.service;

import com.pennywise.dao.AdviceDao;
import com.pennywise.dao.MessageDao;
import com.pennywise.dao.UserDao;
import com.pennywise.exception.ValidationException;
import com.pennywise.model.Advice;
import com.pennywise.model.Bar;
import com.pennywise.model.Message;
import com.pennywise.model.Role;
import com.pennywise.util.Bars;
import com.pennywise.util.Validator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Advice and messages between advisors and users. */
public class AdvisoryService {
    private final AdviceDao advice = new AdviceDao();
    private final MessageDao messages = new MessageDao();
    private final UserDao users = new UserDao();

    public void giveAdvice(int advisorId, int userId, String text) {
        requireClient(userId);
        advice.save(new Advice(0, userId, advisorId, Validator.requireText(text, "Advice", 1000), null));
    }

    /** senderRole is "USER" or "ADVISOR". */
    public void sendMessage(String senderRole, int userId, String text) {
        requireClient(userId);
        messages.save(new Message(0, userId, senderRole, Validator.requireText(text, "Message", 1000), null));
    }

    public List<Advice> adviceFor(int userId) { return advice.findByUser(userId); }
    public List<Advice> allAdvice() { return advice.findAll(); }
    public List<Message> messagesFor(int userId) { return messages.findByUser(userId); }
    public List<Message> allMessages() { return messages.findAll(); }

    /** Messages per client, ready for a bar chart. */
    public List<Bar> messageStats(Map<Integer, String> names) {
        Map<String, Integer> counts = new TreeMap<>();
        for (Message m : messages.findAll()) {
            counts.merge(names.getOrDefault(m.getUserId(), "Unknown"), 1, Integer::sum);
        }
        return Bars.of(counts);
    }

    private void requireClient(int userId) {
        users.findById(userId).filter(u -> u.getRole() == Role.USER)
                .orElseThrow(() -> new ValidationException("Choose a valid client."));
    }
}
