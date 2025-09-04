package com.example.source_credibility_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "source_stats")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceStats {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id = UUID.randomUUID().toString();

    @Column(name = "site", nullable = false, unique = true, length = 255)
    private String site;

    @Column(name = "total_articles", nullable = false)
    private long totalArticles = 0L;

    @Column(name = "fake_by_ai", nullable = false)
    private long fakeByAi = 0L;

    @Column(name = "fake_by_human", nullable = false)
    private long fakeByHuman = 0L;

    @Column(name = "external_reputation", nullable = false)
    private double externalReputation = 0.5d; // 0..1

    @Column(name = "trust_score", nullable = false)
    private double trustScore = 0.5d; // 0..1

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}

