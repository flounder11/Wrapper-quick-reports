package com.rinhack.Wrapper_quick_reports.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getPrincipal();
        String apiSecret = (String) authentication.getCredentials();

        // Здесь должна быть логика проверки API ключа и секрета
        // Например, проверка в базе данных или валидация

        if (isValidApiKeyAndSecret(apiKey, apiSecret)) {
            return new ApiKeyAuthenticationToken(apiKey, apiSecret, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            throw new AuthenticationException("Invalid API Key or Secret") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean isValidApiKeyAndSecret(String apiKey, String apiSecret) {
        // Логика проверки API ключа и секрета
        // Например, проверка в базе данных или валидация
        return "validApiKey".equals(apiKey) && "validApiSecret".equals(apiSecret);
    }
}

