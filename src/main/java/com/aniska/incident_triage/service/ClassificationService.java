package com.aniska.incident_triage.service;

import com.aniska.incident_triage.enums.Priority;
import com.aniska.incident_triage.enums.TicketCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassificationService {

    public TicketCategory classify(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        if (text.contains("payment") || text.contains("billing") || text.contains("refund")) {
            return TicketCategory.PAYMENT;
        } else if (text.contains("login") || text.contains("password") || text.contains("auth")) {
            return TicketCategory.AUTHENTICATION;
        } else if (text.contains("bug") || text.contains("error") || text.contains("crash")) {
            return TicketCategory.BUG;
        } else if (text.contains("feature") || text.contains("enhancement")) {
            return TicketCategory.FEATURE_REQUEST;
        }
        return TicketCategory.GENERAL;
    }

    public Priority determinePriority(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        if (text.contains("urgent") || text.contains("critical") || text.contains("down")) {
            return Priority.CRITICAL;
        } else if (text.contains("important") || text.contains("asap")) {
            return Priority.HIGH;
        } else if (text.contains("minor") || text.contains("low")) {
            return Priority.LOW;
        }
        return Priority.MEDIUM;
    }
}
