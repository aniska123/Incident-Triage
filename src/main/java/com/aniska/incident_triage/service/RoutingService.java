package com.aniska.incident_triage.service;

import com.aniska.incident_triage.enums.TicketCategory;
import com.aniska.incident_triage.model.Team;
import com.aniska.incident_triage.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutingService {

    private final TeamRepository teamRepository;

    public Team route(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        if (text.contains("payment") || text.contains("billing")) {
            return teamRepository.findByName("Payments Team")
                    .orElseGet(() -> getDefaultTeam());
        } else if (text.contains("login") || text.contains("auth")) {
            return teamRepository.findByName("Auth Team")
                    .orElseGet(() -> getDefaultTeam());
        }

        return getDefaultTeam();
    }

    public Team routeByCategory(TicketCategory category) {
        return switch (category) {
            case PAYMENT -> teamRepository.findByName("Payments Team")
                    .orElseGet(() -> getDefaultTeam());
            case AUTH -> teamRepository.findByName("Auth Team")
                    .orElseGet(() -> getDefaultTeam());
            case INFRASTRUCTURE -> teamRepository.findByName("Infrastructure Team")
                    .orElseGet(() -> getDefaultTeam());
            default -> getDefaultTeam();
        };
    }

    private Team getDefaultTeam() {
        return teamRepository.findByName("General Support")
                .orElseThrow(() -> new RuntimeException("Default team not found"));
    }
}
