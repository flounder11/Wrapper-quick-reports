package com.rinhack.Wrapper_quick_reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/templates")
public class TemplatesController {

    @Value("${cloud.api.base-url}")
    private String baseUrl; // Базовый URL облака, задаётся в application.properties

    private final RestTemplate restTemplate;
    @Autowired
    public TemplatesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Загрузка .frx файла в облако с использованием Base64
     */
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadTemplateToRoot(
            @RequestParam("file") MultipartFile file) {
        try {
            // Преобразуем содержимое файла в Base64
            String base64Content = Base64.getEncoder().encodeToString(file.getBytes());

            // Формируем URL для корневой папки
            String url = baseUrl + "/Templates/Root/File";

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "text/plain");
            headers.set("Content-Type", "application/json-patch+json");

            // Подготавливаем данные для отправки
            Map<String, String> fileRequest = new HashMap<>();
            fileRequest.put("name", file.getOriginalFilename()); // Имя файла
            fileRequest.put("content", base64Content);          // Base64-содержимое файла

            HttpEntity<Map<String, String>> request = new HttpEntity<>(fileRequest, headers);

            // Отправляем запрос
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            return ResponseEntity.ok(response.getBody());
        } catch (IOException e) {
            // Обработка ошибок чтения файла
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to process the file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
