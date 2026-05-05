package com.ritvik.zodiacverseBackend.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class PdfGeneratorService {

    public byte[] generateFromHtml(String html) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ConverterProperties props = new ConverterProperties();
            HtmlConverter.convertToPdf(html, out, props);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }
}