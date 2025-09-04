package com.example.source_credibility_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateSourceRequest {
    private String site;      // e.g., domain
    private String headline;  // optional, for context
    private String username;  // originator (needed to update News later)
}

