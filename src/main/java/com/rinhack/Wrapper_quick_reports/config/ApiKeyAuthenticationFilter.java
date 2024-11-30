package com.rinhack.Wrapper_quick_reports.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String apiKeyHeaderName;
    private final String apiSecretHeaderName;

    public ApiKeyAuthenticationFilter(String apiKeyHeaderName, String apiSecretHeaderName) {
        this.apiKeyHeaderName = apiKeyHeaderName;
        this.apiSecretHeaderName = apiSecretHeaderName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader(apiKeyHeaderName);
        String apiSecret = request.getHeader(apiSecretHeaderName);

        if (apiKey != null && apiSecret != null) {
            Authentication auth = new ApiKeyAuthenticationToken(apiKey, apiSecret);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}