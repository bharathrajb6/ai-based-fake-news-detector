package com.example.news_management_service.mapper;

import com.example.news_management_service.dto.request.NewsCheckRequest;
import com.example.news_management_service.dto.response.NewsCheckResponse;
import com.example.news_management_service.model.News;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    /**
     * Converts a {@link NewsCheckRequest} to a {@link News}.
     *
     * @param request the {@link NewsCheckRequest} to convert
     * @return a {@link News} with the given details
     */
    News toNews(NewsCheckRequest request);

    /**
     * Converts a {@link News} to a {@link NewsCheckResponse}.
     *
     * @param news the {@link News} to convert
     * @return a {@link NewsCheckResponse} with the given details
     */
    NewsCheckResponse toNewsCheckResponse(News news);

    /**
     * Converts a page of {@link News} to a page of {@link NewsCheckResponse}.
     *
     * @param news the page of {@link News} to convert
     * @return a page of {@link NewsCheckResponse} with the given details
     */
    default Page<NewsCheckResponse> toPageNewsCheckResponse(Page<News> news) {
        List<NewsCheckResponse> newsCheckResponseList = news.getContent().stream().map(this::toNewsCheckResponse).collect(Collectors.toList());
        return new PageImpl<>(newsCheckResponseList, news.getPageable(), news.getTotalElements());
    }
}
