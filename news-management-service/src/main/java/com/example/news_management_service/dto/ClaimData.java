package com.example.news_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimData {
    private String claim;
    private String result;
    private String evidence;
    private String username;
}
