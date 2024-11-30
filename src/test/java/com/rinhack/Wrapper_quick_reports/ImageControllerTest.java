package com.rinhack.Wrapper_quick_reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.rinhack.Wrapper_quick_reports.models.ImageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGenerateRxf_ValidRequest() throws Exception {
        // Подготовка входных данных
        ImageRequest request = new ImageRequest();
        request.setImages(List.of(
                Base64.getEncoder().encodeToString(getSampleImageBytes()),
                Base64.getEncoder().encodeToString(getSampleImageBytes())
        ));
        request.setWidth(1024);
        request.setHeight(768);
        request.setBorderThickness(10);
        request.setBorderColor("#FF0000");
        request.setShuffle(false);
        request.setColumns(5);

        // Отправка запроса
        mockMvc.perform(post("/api/v1/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pattern.rxf"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    public void testGenerateRxf_InvalidWidth() throws Exception {
        // Подготовка входных данных с некорректной шириной
        ImageRequest request = new ImageRequest();
        request.setImages(List.of(
                Base64.getEncoder().encodeToString(getSampleImageBytes())
        ));
        request.setWidth(0); // Некорректная ширина
        request.setHeight(768);
        request.setBorderThickness(10);
        request.setBorderColor("#FF0000");
        request.setShuffle(false);
        request.setColumns(5);

        // Отправка запроса
        mockMvc.perform(post("/api/v1/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateRxf_EmptyImages() throws Exception {
        // Подготовка входных данных с пустым списком изображений
        ImageRequest request = new ImageRequest();
        request.setImages(Collections.emptyList());
        request.setWidth(1024);
        request.setHeight(768);
        request.setBorderThickness(10);
        request.setBorderColor("#FF0000");
        request.setShuffle(false);
        request.setColumns(5);

        // Отправка запроса
        mockMvc.perform(post("/api/v1/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateRxf_InvalidBorderColor() throws Exception {
        // Подготовка входных данных с некорректным цветом
        ImageRequest request = new ImageRequest();
        request.setImages(List.of(
                Base64.getEncoder().encodeToString(getSampleImageBytes())
        ));
        request.setWidth(1024);
        request.setHeight(768);
        request.setBorderThickness(10);
        request.setBorderColor("INVALID_COLOR"); // Некорректный цвет
        request.setShuffle(false);
        request.setColumns(5);

        // Отправка запроса
        mockMvc.perform(post("/api/v1/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateRxf_ExcessiveColumns() throws Exception {
        // Подготовка входных данных с колонками, превышающими ширину
        ImageRequest request = new ImageRequest();
        request.setImages(List.of(
                Base64.getEncoder().encodeToString(getSampleImageBytes())
        ));
        request.setWidth(1024);
        request.setHeight(768);
        request.setBorderThickness(10);
        request.setBorderColor("#FF0000");
        request.setShuffle(false);
        request.setColumns(100); // Чрезмерное количество колонок

        // Отправка запроса
        mockMvc.perform(post("/api/v1/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private byte[] getSampleImageBytes() throws IOException {
        // Генерация тестового изображения
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
