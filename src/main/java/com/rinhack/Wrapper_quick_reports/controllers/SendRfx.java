package com.rinhack.Wrapper_quick_reports.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SendRfx {
    public static void main(String[] args) {
        String url = "https://hygieia.fast-report.com/api/rp/v1/Templates/Folder/5fa919f9292a8300019349b9/File";

        // Создаем объект RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        headers.set("accept", "text/plain");

        // Создаем тело запроса
        String requestBody = "{\"name\": \"template.frx\", \"content\": \"77u/PD94bWwgdmVyc2lvbj0iMS4wIiB\"}";

        // Создаем HttpEntity с заголовками и телом запроса
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Выполняем POST-запрос
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Выводим ответ
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());
    }
}