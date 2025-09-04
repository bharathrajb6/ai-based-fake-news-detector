package com.example.source_credibility_service.service;

import com.example.source_credibility_service.dto.request.EvaluateSourceRequest;
import com.example.source_credibility_service.dto.response.SourceCredibilityResponse;
import com.example.source_credibility_service.model.SourceStats;
import com.example.source_credibility_service.repo.SourceStatsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourceCredibilityService {

    private final SourceStatsRepo sourceStatsRepo;

    /**
     * Computes/updates the trust score for a given site.
     * Formula (simple baseline):
     *   historyScore = 1 - min(1, (fakeByAi + 1.5*fakeByHuman) / max(1, totalArticles))
     *   trustScore   = 0.6*historyScore + 0.4*externalReputation
     */
    public SourceCredibilityResponse evaluate(EvaluateSourceRequest request) {
        if (request == null || request.getSite() == null) {
            throw new IllegalArgumentException("site is required");
        }

        SourceStats stats = sourceStatsRepo.findBySite(request.getSite()).orElseGet(() -> {
            SourceStats s = new SourceStats();
            s.setSite(request.getSite());
            return s;
        });

        // compute historyScore
        double denominator = Math.max(1d, (double) stats.getTotalArticles());
        double weightedFake = stats.getFakeByAi() + 1.5d * stats.getFakeByHuman();
        double historyScore = 1d - Math.min(1d, weightedFake / denominator);

        double trustScore = 0.6d * historyScore + 0.4d * stats.getExternalReputation();
        stats.setTrustScore(trustScore);
        stats.setUpdatedAt(OffsetDateTime.now());

        sourceStatsRepo.save(stats);

        log.info("Evaluated trust score for site {} = {}", stats.getSite(), stats.getTrustScore());

        SourceCredibilityResponse response = new SourceCredibilityResponse(
                stats.getSite(),
                stats.getTotalArticles(),
                stats.getFakeByAi(),
                stats.getFakeByHuman(),
                stats.getExternalReputation(),
                stats.getTrustScore(),
                request.getHeadline(),
                request.getUsername()
        );
        return response;
    }
}

