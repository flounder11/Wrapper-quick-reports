package com.rinhack.Wrapper_quick_reports.fastreports;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gift-wrap")
public class GiftWrapController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        try {
            // Сохраняем загруженные изображения
            List<String> savedPaths = new ArrayList<>();
            for (MultipartFile image : images) {
                String filePath = saveImage(image);
                savedPaths.add(filePath);
            }
            return ResponseEntity.ok("Images uploaded successfully: " + savedPaths);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload images: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePDF(
            @RequestParam("template") String templatePath,
            @RequestParam("shuffle") boolean shuffle) {
        try {
            // Генерация PDF
            byte[] pdfBytes = generateGiftWrapPDF(templatePath, shuffle);

            // Возвращаем PDF в ответе
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("gift_wrap.pdf").build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = uploadDir + image.getOriginalFilename();
        File file = new File(filePath);
        image.transferTo(file);
        return filePath;
    }

    private byte[] generateGiftWrapPDF(String templatePath, boolean shuffle) throws Exception {
        // Чтение шаблона .frx и генерация PDF
        return PDFGenerator.generate(templatePath, shuffle);
    }
}
