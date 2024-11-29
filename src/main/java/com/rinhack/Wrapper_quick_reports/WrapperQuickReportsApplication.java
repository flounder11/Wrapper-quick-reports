package com.rinhack.Wrapper_quick_reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WrapperQuickReportsApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(WrapperQuickReportsApplication.class, args);
		try {
			Thread.currentThread().join(); // Оставляет основной поток активным
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
