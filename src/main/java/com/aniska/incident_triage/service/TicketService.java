package com.aniska.incident_triage.service;

import com.aniska.incident_triage.enums.Priority;
import com.aniska.incident_triage.enums.TicketCategory;
import com.aniska.incident_triage.enums.TicketStatus;
import com.aniska.incident_triage.model.Team;
import com.aniska.incident_triage.model.Ticket;
import com.aniska.incident_triage.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ClassificationService classificationService;
    private final RoutingService routingService;

    @Transactional
    public Ticket createTicket(String title, String description) {
        log.info("Creating ticket: '{}'", title);

        TicketCategory category = classificationService.classify(title, description);
        Priority priority = classificationService.determinePriority(title, description);
        Team team = routingService.route(title, description);

        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .category(category)
                .priority(priority)
                .team(team)
                .status(TicketStatus.OPEN)
                .build();

        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> getTicketsByPriority(Priority priority) {
        return ticketRepository.findByPriorityOrderByCreatedAtAsc(priority);
    }

    public List<Ticket> getTicketsByTeam(Long teamId) {
        return ticketRepository.findByTeamId(teamId);
    }

    @Transactional
    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getTicketById(id);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found with ID: " + id);
        }
        ticketRepository.deleteById(id);
    }

    public long countByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long countSlaBreached() {
        return ticketRepository.countBySlaBreachedTrue();
    }
}
