package com.aniska.incident_triage.repository;

import com.aniska.incident_triage.enums.Priority;
import com.aniska.incident_triage.enums.TicketStatus;
import com.aniska.incident_triage.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriorityOrderByCreatedAtAsc(Priority priority);
    List<Ticket> findByTeamId(Long teamId);
    long countByStatus(TicketStatus status);
    long countBySlaBreachedTrue();
}
