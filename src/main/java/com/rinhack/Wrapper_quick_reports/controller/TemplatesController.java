package com.rinhack.Wrapper_quick_reports.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplatesController {

    @Value("${cloud.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public TemplatesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/root")
    public ResponseEntity<String> getRootFolder(@RequestParam(required = false) String subscriptionId) {
        String url = baseUrl + "/Templates/Root";
        if (subscriptionId != null) {
            url += "?subscriptionId=" + subscriptionId;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "text/plain");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response;
    }

    @PostMapping("/folder/{folderId}/file")
    public ResponseEntity<Map<String, Object>> uploadTemplate(
            @PathVariable String folderId,
            @RequestBody Map<String, String> fileRequest) {

        String url = baseUrl + "/Templates/Folder/" + folderId + "/File";
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "text/plain");
        headers.set("Content-Type", "application/json-patch+json");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(fileRequest, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return ResponseEntity.ok(response.getBody());
    }
}

