package com.rinhack.Wrapper_quick_reports.config;

import com.rinhack.Wrapper_quick_reports.models.PatternInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean(initMethod = "initialize")
    public PatternInitializer init() {
        return new PatternInitializer();
    }
}

