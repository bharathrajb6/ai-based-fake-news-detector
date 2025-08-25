package com.example.news_management_service.mapper;

import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
import com.example.news_management_service.model.News;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News toNews(NewsCheckRequest request);

    NewsCheckResponse toNewsCheckResponse(News news);

    default Page<NewsCheckResponse> toPageNewsCheckResponse(Page<News> news) {
        List<NewsCheckResponse> newsCheckResponseList = news.getContent().stream().map(this::toNewsCheckResponse).collect(Collectors.toList());
        return new PageImpl<>(newsCheckResponseList, news.getPageable(), news.getTotalElements());
    }
}
