package com.example.news_management_service.mapper;

import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
import com.example.news_management_service.model.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News toNews(NewsCheckRequest request);

    NewsCheckResponse toNewsCheckResponse(News news);
}
