package com.rinhack.Wrapper_quick_reports.models;

import com.itextpdf.io.source.ByteArrayOutputStream;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Pattern {
    private String patterName;
    private String patternXml;
    private List<String> base64Images;
    private int width;
    private int height;
    private int borderThickness;
    private String borderColorHex;
    private boolean shuffle;
    private int columns;

    public Pattern(String patternName,List<String> base64Images, int width, int height, int borderThickness, String borderColorHex, boolean shuffle, int column) {
        this.patterName = patternName;
        this.base64Images = base64Images;
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.borderColorHex = borderColorHex;
        this.shuffle = shuffle;
        this.columns = column;
    }

    protected void generatePattern() {

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
//        if (shuffle) {
//            Collections.shuffle(base64Images);
//        }

        // Добавляем объекты изображений в отчет
        frxContent.append("<PictureObject Name=\"Picture-1").append("\" Left=\"")
                .append(0).append("\" Top=\"").append(0).append("\" Width=\"")
                .append(width).append("\" Height=\"").append(height)
                .append("\" ImageFormat=\"Png\" Image=\"").append("\"/>\n");
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
                        .append("\" ImageFormat=\"Png\" Image=\"").append("\"/>\n");

                // Переходим к следующему изображению
                imageIndex = (imageIndex + 1) % base64Images.size();
            }
        }

        // Завершаем описание данных и страницы
        frxContent.append("</DataBand>\n")

                .append("</ReportPage>\n")
                .append("</Report>\n");

        this.patternXml =  frxContent.toString();
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
    public String toDecodedFile(){
        if (patternXml.equals(null)){
            return "empty xml";
        }
        return Base64.getEncoder().encodeToString(patternXml.getBytes());
    }
}