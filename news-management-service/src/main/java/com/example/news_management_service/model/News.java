package com.example.news_management_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "headline", nullable = false, unique = true)
    private String headline;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "result")
    private String result;

    @Column(name = "credibility_score")
    private Long credibilityScore;

    @Column(name = "evidence")
    private String evidence;

}
