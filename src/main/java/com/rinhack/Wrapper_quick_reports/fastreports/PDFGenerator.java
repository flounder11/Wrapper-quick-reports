package com.rinhack.Wrapper_quick_reports.fastreports;

import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PDFGenerator {
    public static byte[] generate(String templatePath, boolean shuffle) throws Exception {

        Properties templateProps = new Properties();
        templateProps.load(new FileInputStream(templatePath));

        float paperWidth = Float.parseFloat(templateProps.getProperty("PAPER_WIDTH"));
        float paperHeight = Float.parseFloat(templateProps.getProperty("PAPER_HEIGHT"));
        int columns = Integer.parseInt(templateProps.getProperty("COLUMNS"));
        int rows = Integer.parseInt(templateProps.getProperty("ROWS"));
        float borderThickness = Float.parseFloat(templateProps.getProperty("BORDER_THICKNESS"));
        DeviceRgb borderColor = new DeviceRgb(
                Integer.parseInt(templateProps.getProperty("BORDER_COLOR_RED")),
                Integer.parseInt(templateProps.getProperty("BORDER_COLOR_GREEN")),
                Integer.parseInt(templateProps.getProperty("BORDER_COLOR_BLUE"))
        );

        List<String> images = getUploadedImages();
        if (shuffle) {
            Collections.shuffle(images);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, new PageSize(paperWidth, paperHeight));

        float cellWidth = paperWidth / columns;
        float cellHeight = paperHeight / rows;

        int imageIndex = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ImageData imageData = ImageDataFactory.create(images.get(imageIndex % images.size()));
                Image img = new Image(imageData);
                img.setWidth(UnitValue.createPointValue(cellWidth * 2.83f));
                img.setHeight(cellHeight* 2.83f);
                img.setBorder(new SolidBorder(borderColor, borderThickness));
                document.add(img);
                imageIndex++;
            }
        }

        document.close();
        return baos.toByteArray();
    }

    private static List<String> getUploadedImages() {
        return List.of("uploads/img1.png", "uploads/img2.png", "uploads/img3.png");
    }
}
