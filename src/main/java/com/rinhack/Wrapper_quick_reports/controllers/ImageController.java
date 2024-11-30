package com.rinhack.Wrapper_quick_reports.controllers;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.rinhack.Wrapper_quick_reports.models.ImageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateRxf(@RequestBody ImageRequest request) {
        if (request.getImages() == null || request.getImages().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            byte[] rxfFile = generateRxfFile(
                    request.getImages(),
                    request.getWidth(),
                    request.getHeight(),
                    request.getBorderThickness(),
                    request.getBorderColor(),
                    request.isShuffle(),
                    request.getColumns()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pattern.rxf")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(rxfFile);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    private byte[] generateRxfFile(
            List<String> base64Images,
            int width,
            int height,
            int borderThickness,
            String borderColorHex,
            boolean shuffle,
            int columns
    ) throws IOException {

        // Перемешиваем изображения, если нужно
        if (shuffle) {
            Collections.shuffle(base64Images);
        }

        // Декодируем изображения
        List<BufferedImage> decodedImages = new ArrayList<>();
        for (String base64Image : base64Images) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            decodedImages.add(ImageIO.read(new ByteArrayInputStream(decodedBytes)));
        }

        // Создаем холст для упаковочной бумаги
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();

        // Устанавливаем цвет фона
        graphics.setColor(Color.WHITE);
        try{
            graphics.fillRect(0, 0, width, height);
        } catch (Exception e){
            throw new IOException(e.getMessage());
        }


        // Размер одной ячейки
        int cellWidth = width / columns;
        int cellHeight = cellWidth; // Условие для квадратных ячеек

        int rows = height / cellHeight;

        // Устанавливаем цвет границ
        Color borderColor = null;
        try {
            borderColor = Color.decode(borderColorHex);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        // Заполнение холста узором
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Получаем текущее изображение
                BufferedImage img = decodedImages.get((row * columns + col) % decodedImages.size());

                int x = col * cellWidth;
                int y = row * cellHeight;

                // Рисуем границу
                graphics.setColor(borderColor);
                graphics.fillRect(x, y, cellWidth, cellHeight);

                // Рисуем изображение
                int imgX = x + borderThickness;
                int imgY = y + borderThickness;
                int imgWidth = cellWidth - 2 * borderThickness;
                int imgHeight = cellHeight - 2 * borderThickness;
                graphics.drawImage(img, imgX, imgY, imgWidth, imgHeight, null);
            }
        }

        // Завершаем работу с графикой
        graphics.dispose();

        // Сохраняем изображение как PNG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", baos);

        return baos.toByteArray();
    }
}
