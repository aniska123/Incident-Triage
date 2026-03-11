package com.aniska.incident_triage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a team that handles incident tickets.
 * Teams are matched to tickets based on skill_tags and routing rules.
 */
@Entity
@Table(name = "teams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Team name e.g. "Payments Team", "Auth Team"
    @Column(nullable = false, unique = true)
    private String name;

    // Skills this team handles e.g. ["payment", "billing", "refund"]
    @Column(name = "skill_tags", columnDefinition = "text[]")
    private String[] skillTags;

    // Contact email for the team
    @Column(name = "email")
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // One team can have many tickets assigned
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets;

    // One team can have many routing rules
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RoutingRule> routingRules;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}