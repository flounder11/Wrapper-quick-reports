package com.rinhack.Wrapper_quick_reports.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
public class ImageRequest {
    private List<String> images; // Изображения в формате Base64
    private int width;           // Ширина упаковочной бумаги
    private int height;          // Высота упаковочной бумаги
    private int borderThickness; // Толщина границы вокруг каждого изображения
    private String borderColor;  // Цвет границы (HEX)
    private boolean shuffle;     // Перемешивать изображения
    private int columns;         // Количество колонок


}
