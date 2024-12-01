package com.rinhack.Wrapper_quick_reports.controllers;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.rinhack.Wrapper_quick_reports.models.ImageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    @Value("${fastrep.app.apitok}")
    protected static String apitok;
    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateFiles(@RequestBody ImageRequest request) {
        if (request.getImages() == null || request.getImages().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            // Генерируем изображение
            byte[] imageFile = generateImageFile(
                    request.getImages(),
                    request.getWidth(),
                    request.getHeight(),
                    request.getBorderThickness(),
                    request.getBorderColor(),
                    request.isShuffle(),
                    request.getColumns()
            );

            // Генерируем FRX
            byte[] frxFile = generateFrxFile(
                    request.getImages(),
                    request.getWidth(),
                    request.getHeight(),
                    request.getBorderThickness(),
                    request.getBorderColor(),
                    request.isShuffle(),
                    request.getColumns()
            );

            // Создаем ZIP-архив
            ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
            try (ZipOutputStream zipOut = new ZipOutputStream(zipBaos)) {
                // Добавляем PNG-файл в архив
                ZipEntry imageEntry = new ZipEntry("pattern.png");
                zipOut.putNextEntry(imageEntry);
                zipOut.write(imageFile);
                zipOut.closeEntry();

                // Добавляем FRX-файл в архив
                ZipEntry frxEntry = new ZipEntry("pattern.frx");
                zipOut.putNextEntry(frxEntry);
                zipOut.write(frxFile);
                zipOut.closeEntry();
            }

            // Возвращаем архив
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pattern.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBaos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private byte[] generateImageFile(
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

        // Устанавливаем цвет фона, совпадающий с цветом границ
        Color borderColor = Color.decode(borderColorHex);
        graphics.setColor(borderColor);
        graphics.fillRect(0, 0, width, height);

        // Размер одной ячейки
        int cellWidth = width / columns;
        int cellHeight = cellWidth; // Начальный размер ячейки (позже учитываем соотношение)

        int rows = height / cellHeight;

        // Заполнение холста узором
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Проверяем, не вышли ли за пределы последнего ряда
                int y = row * cellHeight;
                if (y + cellHeight > height) {
                    cellHeight = height - y; // Уменьшаем ячейку для последнего ряда
                }

                int x = col * cellWidth;
                if (x + cellWidth > width) {
                    break; // Если ширина выходит за границу, пропускаем
                }

                // Получаем текущее изображение
                BufferedImage img = decodedImages.get((row * columns + col) % decodedImages.size());

                // Рассчитываем размеры изображения с учетом сохранения соотношения сторон
                int originalWidth = img.getWidth();
                int originalHeight = img.getHeight();
                double aspectRatio = (double) originalWidth / originalHeight;

                int imgWidth, imgHeight;
                if (aspectRatio > 1) { // Широкое изображение
                    imgWidth = cellWidth - 2 * borderThickness;
                    imgHeight = (int) (imgWidth / aspectRatio);
                } else { // Высокое изображение
                    imgHeight = cellHeight - 2 * borderThickness;
                    imgWidth = (int) (imgHeight * aspectRatio);
                }

                // Центрируем изображение внутри ячейки
                int imgX = x + (cellWidth - imgWidth) / 2;
                int imgY = y + (cellHeight - imgHeight) / 2;

                // Рисуем изображение
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
        private byte[] generateFrxFile(
                List<String> base64Images,
                int width,
                int height,
                int borderThickness,
                String borderColorHex,
                boolean shuffle,
                int columns
        ) {
            StringBuilder frxContent = new StringBuilder();

            // Начало XML документа
            frxContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                    .append("<Report ScriptLanguage=\"CSharp\" ReportInfo.Created=\"11/30/2024 13:58:08\" ReportInfo.Modified=\"11/30/2024 13:58:08\" ReportInfo.CreatorVersion=\"2025.1.1.0\">\n")
                    .append("<Dictionary/>\n");

            // Описание страницы
            frxContent.append(String.format("<ReportPage Name=\"Page1\" Guides=\"%s\" Watermark.Font=\"Arial, 60pt\">\n",width));

            // Основная часть отчета с изображениями
            frxContent.append("<DataBand Name=\"Data1\" Width=\"").append(width).append(String.format("\" Height=\"%s\">\n", height));

            // Размер одной ячейки
            int cellWidth = width / columns;
            int cellHeight = cellWidth; // Начальный размер ячейки

            int rows = height / cellHeight;

            // Если необходимо, перемешиваем список изображений
            if (shuffle) {
                Collections.shuffle(base64Images);
            }

            // Добавляем объекты изображений в отчет
            frxContent.append("<PictureObject Name=\"Picture-1").append("\" Left=\"")
                    .append(0).append("\" Top=\"").append(0).append("\" Width=\"")
                    .append(width).append("\" Height=\"").append(height)
                    .append("\" ImageFormat=\"Png\" Image=\"").append(hexToBase64Image(borderColorHex)).append("\"/>\n");
            int imageIndex = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    // Позиция текущего изображения
                    int x = col * cellWidth;
                    int y = row * cellHeight;

                    // Расчет параметров для изображения
                    BufferedImage img = getDecodedImage(base64Images.get(imageIndex));
                    double aspectRatio = (double) img.getWidth() / img.getHeight();

                    int imgWidth = cellWidth - 2 * borderThickness;
                    int imgHeight = (int) (imgWidth / aspectRatio);
                    if (imgHeight > cellHeight - 2 * borderThickness) {
                        imgHeight = cellHeight - 2 * borderThickness;
                        imgWidth = (int) (imgHeight * aspectRatio);
                    }

                    // Центрирование изображения внутри ячейки
                    int imgX = x + (cellWidth - imgWidth) / 2;
                    int imgY = y + (cellHeight - imgHeight) / 2;

                    // Получаем Base64 без заголовков
                    String cleanBase64 = base64Images.get(imageIndex).replace("data:image/png;base64,", "");

                    // Добавляем объект изображения в XML
                    frxContent.append("<PictureObject Name=\"Picture").append(imageIndex + 1).append("\" Left=\"")
                            .append(imgX).append("\" Top=\"").append(imgY).append("\" Width=\"")
                            .append(imgWidth).append("\" Height=\"").append(imgHeight)
                            .append("\" ImageFormat=\"Png\" Image=\"").append(cleanBase64).append("\"/>\n");

                    // Переходим к следующему изображению
                    imageIndex = (imageIndex + 1) % base64Images.size();
                }
            }

            // Завершаем описание данных и страницы
            frxContent.append("</DataBand>\n")

                    .append("</ReportPage>\n")
                    .append("</Report>\n");

            // Преобразуем строку в байты и возвращаем
            return frxContent.toString().getBytes();
        }
        private BufferedImage getDecodedImage(String base64Image) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                return ImageIO.read(new ByteArrayInputStream(decodedBytes));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    public static String hexToBase64Image(String hexColor) {
        try {
            // Проверяем корректность hex-цвета
            if (!hexColor.matches("#[a-fA-F0-9]{6}")) {
                throw new IllegalArgumentException("Invalid hex color format. Use #RRGGBB format.");
            }

            // Размер создаваемого изображения (например, 100x100 пикселей)
            int width = 100;
            int height = 100;

            // Создаем цвет на основе hex-строки
            Color color = Color.decode(hexColor);

            // Создаем BufferedImage и рисуем прямоугольник указанного цвета
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
            graphics.dispose();

            // Кодируем изображение в Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream); // Сохраняем как PNG
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void postToFastReportTemplates(String filename, String encodedContent) {
        try {
            // Создаем тело запроса
            String requestBody = String.format("""
                {
                  "name": "%s",
                  "content": "%s"
                }
            """,filename, encodedContent);

            // Создаем HTTP клиент
            HttpClient client = HttpClient.newHttpClient();

            // Создаем HTTP запрос
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create("https://hygieia.fast-report.com/api/rp/v1/Templates/Folder/674986111d6ee7f62ddcd20e/File"))
                    .header("accept", "text/plain")
                    .header("Content-Type", "application/json-patch+json")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.format("apikey:%s", apitok).getBytes()))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Отправляем запрос и получаем ответ
            HttpResponse<String> response = client.send((java.net.http.HttpRequest) request, HttpResponse.BodyHandlers.ofString());

            // Печатаем результат
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

