package com.rinhack.Wrapper_quick_reports;

import com.rinhack.Wrapper_quick_reports.models.PatternInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WrapperQuickReportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WrapperQuickReportsApplication.class, args);
		new PatternInitializer();
	}

}
