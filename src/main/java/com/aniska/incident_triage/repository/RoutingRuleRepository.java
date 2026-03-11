package com.aniska.incident_triage.repository;

import com.aniska.incident_triage.model.RoutingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutingRuleRepository extends JpaRepository<RoutingRule, Long> {
    
    @Query("SELECT r FROM RoutingRule r WHERE r.keyword IN :keywords")
    List<RoutingRule> findByKeywords(@Param("keywords") List<String> keywords);
}
