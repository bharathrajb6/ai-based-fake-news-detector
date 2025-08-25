package com.example.news_management_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Returns a {@link SecurityWebFilterChain} that allows any exchange to be
     * permitted.
     *
     * @param security the server HTTP security configuration
     * @return a security web filter chain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security) {
        return security.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(exchange ->
                        exchange
                                .anyExchange().permitAll()).
                build();
    }
}
