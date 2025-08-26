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

    /**
     * Finds a news article by its headline.
     *
     * @param headline the headline of the news article to search for
     * @return an Optional containing the news article if found, or an empty Optional if not found
     */
    Optional<News> findByHeadline(String headline);

    /**
     * Finds a news article by its headline and username.
     *
     * @param headline the headline of the news article to search for
     * @param username the username of the user who posted the news article
     * @return a News object containing the news article if found, or null if not found
     */
    @Query("SELECT n FROM News n WHERE n.headline = ?1 AND n.username = ?2")
    News findByHeadLineAndUsername(String headline, String username);


    /**
     * Updates the result and evidence of a news article with the given headline and username.
     * <p>
     * This method performs an update query on the News table, setting the result and evidence of the news article
     * with the given headline and username to the given values. The method returns a boolean indicating whether
     * at least one record was updated.
     *
     * @param result   the new result of the news article
     * @param evidence the new evidence of the news article
     * @param headline the headline of the news article to update
     * @param username the username of the user who posted the news article
     * @return true if at least one record was updated, false otherwise
     */
    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.result = ?1, n.evidence = ?2 WHERE n.headline = ?3 AND n.username = ?4")
    void updateNewsDetails(String result, String evidence, String headline, String username);

    /**
     * Finds all news articles by a given username.
     *
     * @param username the username of the user to search for
     * @param pageable the pagination object
     * @return a Page of News objects containing the news articles if found, or an empty Page if not found
     */
    @Query("SELECT n FROM News n WHERE n.username = ?1")
    Page<News> findAllNewsByUsername(String username, Pageable pageable);
}
