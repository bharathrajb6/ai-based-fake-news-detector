package com.example.source_credibility_service.repo;

import com.example.source_credibility_service.model.SourceStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceStatsRepo extends JpaRepository<SourceStats, String> {
    Optional<SourceStats> findBySite(String site);
}

