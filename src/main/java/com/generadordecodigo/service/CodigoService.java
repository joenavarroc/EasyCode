package com.generadordecodigo.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.pdf417.PDF417Writer;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class CodigoService {

    public BufferedImage generarCodigo(String texto, String tipo) throws Exception {
        tipo = tipo.trim().toUpperCase();
        BitMatrix matrix;
        BarcodeFormat formato;

        switch (tipo) {
            case "QR":
            case "QR_CODE":
                formato = BarcodeFormat.QR_CODE;
                matrix = new QRCodeWriter().encode(texto, formato, 250, 250);
                break;
            case "CODE_128":
                formato = BarcodeFormat.CODE_128;
                matrix = new Code128Writer().encode(texto, formato, 300, 100);
                break;
            case "EAN_13":
                texto = completarEAN13SiFalta(texto);
                if (!texto.matches("\\d{13}"))
                    throw new IllegalArgumentException("EAN-13 debe tener 13 dígitos.");
                if (!validarChecksumEAN13(texto))
                    throw new IllegalArgumentException("Dígito de control EAN-13 inválido.");
                formato = BarcodeFormat.EAN_13;
                matrix = new EAN13Writer().encode(texto, formato, 300, 100);
                break;
            case "UPC_A":
                if (!texto.matches("\\d{11,12}"))
                    throw new IllegalArgumentException("UPC-A debe tener 11 o 12 dígitos.");
                if (texto.length() == 12 && !validarChecksumUPCA(texto))
                    throw new IllegalArgumentException("Dígito de control UPC-A inválido.");
                if (texto.length() == 11) {
                    texto = completarConChecksumUPCA(texto);
                }
                formato = BarcodeFormat.UPC_A;
                matrix = new UPCAWriter().encode(texto, formato, 300, 100);
                break;
            
            case "CODE_39":
                formato = BarcodeFormat.CODE_39;
                matrix = new Code39Writer().encode(texto, formato, 300, 100);
                break;
            case "PDF_417":
                formato = BarcodeFormat.PDF_417;
                matrix = new PDF417Writer().encode(texto, formato, 300, 150);
                break;
            default:
                throw new IllegalArgumentException("Tipo de código no soportado: " + tipo);
        }

        // Genera imagen del código
        BufferedImage codigo = MatrixToImageWriter.toBufferedImage(matrix);

        // Imagen con espacio extra para el texto
        int altoExtra = 20;
        BufferedImage conTexto = new BufferedImage(codigo.getWidth(), codigo.getHeight() + altoExtra, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = conTexto.createGraphics();

        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, conTexto.getWidth(), conTexto.getHeight());

        // Dibuja el código
        g2d.drawImage(codigo, 0, 0, null);

        // Agrega el texto centrado
        g2d.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, 14);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(texto);
        int x = (codigo.getWidth() - textWidth) / 2;
        int y = codigo.getHeight() + metrics.getAscent();
        g2d.drawString(texto, x, y);

        g2d.dispose();
        return conTexto;
    }

    public String validarContenido(String texto, String tipo) {
        tipo = tipo.trim().toUpperCase();
        switch (tipo) {
            case "QR_CODE":
                return "✅ QR válido.";
            case "CODE_128":
                return texto.matches("[\\x00-\\x7F]+") ? "✅ CODE_128 válido." : "❌ Solo caracteres ASCII permitidos.";
            case "EAN_13":
                if (!texto.matches("\\d{12,13}")) return "❌ Debe tener 12 o 13 dígitos.";
                if (texto.length() == 13 && !validarChecksumEAN13(texto)) return "❌ Dígito de control inválido.";
                return "✅ EAN_13 válido.";
            case "UPC_A":
                return texto.matches("\\d{11,12}") ? "✅ UPC-A válido." : "❌ Debe tener 11 o 12 dígitos.";
            case "CODE_39":
                return texto.matches("[0-9A-Z \\-\\.\\$/\\+%]*") ? "✅ CODE_39 válido." : "❌ Solo caracteres permitidos para CODE_39.";
            case "PDF_417":
                return "✅ PDF_417 válido.";
            default:
                return "❌ Tipo de código no soportado.";
        }
    }

    private boolean validarChecksumEAN13(String codigo) {
        if (!codigo.matches("\\d{13}")) return false;
        int suma = 0;
        for (int i = 0; i < 12; i++) {
            int dig = Character.getNumericValue(codigo.charAt(i));
            suma += (i % 2 == 0) ? dig : dig * 3;
        }
        int calculado = (10 - (suma % 10)) % 10;
        return calculado == Character.getNumericValue(codigo.charAt(12));
    }

    public String completarEAN13SiFalta(String texto) {
        if (texto.length() == 12 && texto.matches("\\d{12}")) {
            int suma = 0;
            for (int i = 0; i < 12; i++) {
                int dig = Character.getNumericValue(texto.charAt(i));
                suma += (i % 2 == 0) ? dig : dig * 3;
            }
            int checkDigit = (10 - (suma % 10)) % 10;
            return texto + checkDigit;
        }
        return texto;
    }

    // UPC-A: Calcula y completa el dígito de control si faltara
    public String completarConChecksumUPCA(String texto11) {
        if (!texto11.matches("\\d{11}"))
            throw new IllegalArgumentException("UPC-A debe tener exactamente 11 dígitos para completar.");
        int sumaImpares = 0, sumaPares = 0;
        for (int i = 0; i < 11; i++) {
            int digito = Character.getNumericValue(texto11.charAt(i));
            if ((i % 2) == 0) sumaImpares += digito; // posiciones impares (0-based)
            else sumaPares += digito;
        }
        int total = (sumaImpares * 3) + sumaPares;
        int checksum = (10 - (total % 10)) % 10;
        return texto11 + checksum;
    }
    public boolean validarChecksumUPCA(String codigo) {
        if (!codigo.matches("\\d{12}")) return false;
        String generado = completarConChecksumUPCA(codigo.substring(0, 11));
        return generado.equals(codigo);
    }  

    public String generarEAN13Valido() {
        Random rand = new Random();
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            base.append(rand.nextInt(10));
        }
        return completarEAN13SiFalta(base.toString());
    }
}
