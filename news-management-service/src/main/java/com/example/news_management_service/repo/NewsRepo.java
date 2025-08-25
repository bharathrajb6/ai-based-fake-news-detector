package com.example.news_management_service.repo;

import com.example.news_management_service.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface NewsRepo extends JpaRepository<News, String> {

    Optional<News> findByHeadline(String headline);

    @Query("SELECT n FROM News n WHERE n.headline = ?1 AND n.username = ?2")
    News findByHeadLineAndUsername(String headline, String username);


    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.result = ?1, n.evidence = ?2 WHERE n.headline = ?3 AND n.username = ?4")
    boolean updateNewsDetails(String result, String evidence, String headline, String username);

    @Query("SELECT n FROM News n WHERE n.username = ?1")
    Page<News> findAllNewsByUsername(String username, Pageable pageable);
}
