package com.rinhack.Wrapper_quick_reports.models;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import static com.rinhack.Wrapper_quick_reports.controllers.ImageController.postToFastReportTemplates;
@Slf4j
public class PatternInitializer {
    private List<Pattern> patterns;
    static int count = 0;

    public void initialize() {


        List<String> images = List.of(
                imageto64("/home/siderea/SpringBoot/Wrapper-quick-reports/src/main/resources/static/background1.jpg"),
                imageto64("/home/siderea/SpringBoot/Wrapper-quick-reports/src/main/resources/static/background1.jpg"),

                imageto64("/home/siderea/SpringBoot/Wrapper-quick-reports/src/main/resources/static/background1.jpg")
        );

        Pattern packNewYearPattern = new Pattern(
                "basicPack",
                images,
                3200,
                2000,
                6,
                "#000000",
                true,
                20
        );

        packNewYearPattern.generatePattern();
//        postToFastReportTemplates(String.format("%s-%d.frx", packNewYearPattern.getPatterName(), count), packNewYearPattern.toDecodedFile());
        log.info(String.format("%s-%d.frx", packNewYearPattern.getPatterName(), count));

    }

    public String imageto64(String filePath) {
        // Путь к файлу в папке ресурсов

        try {
            // Преобразуем файл в Base64
            String base64Encoded = encodeFileToBase64(filePath);
            // Вывод результата
            return base64Encoded;
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }
        return null;
    }
private static String encodeFileToBase64(String filePath) throws IOException {
    File file = new File(filePath);
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
        byte[] fileBytes = new byte[(int) file.length()];
        int bytesRead = fileInputStream.read(fileBytes);
        if (bytesRead != file.length()) {
            throw new IOException("Не удалось считать весь файл");
        }
        // Кодируем в Base64
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }
}



