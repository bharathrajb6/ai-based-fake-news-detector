package com.example.news_management_service.repo;

import com.example.news_management_service.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NewsRepo extends JpaRepository<News, String> {

    Optional<News> findByHeadline(String headline);
}
