package com.generadordecodigo.controller;

import com.generadordecodigo.service.CodigoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/codigo")
public class CodigoController {

    @Autowired
    private CodigoService codigoService;

    @PostMapping("/validar")
    public Map<String, String> validarContenido(@RequestBody Map<String, String> payload) {
        String texto = payload.get("texto");
        String tipo = payload.get("tipo");
        String resultado = codigoService.validarContenido(texto, tipo);
        Map<String, String> response = new HashMap<>();
        response.put("resultado", resultado);
        return response;
    }

    @GetMapping("/generar")
    public ResponseEntity<byte[]> generarCodigo(
            @RequestParam String texto,
            @RequestParam String tipo) {
        try {
            BufferedImage imagen = codigoService.generarCodigo(texto, tipo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, "png", baos);
            byte[] imagenBytes = baos.toByteArray();
    
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(imagenBytes);
    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    

    @GetMapping("/generar-ean")
    public Map<String, String> generarEAN13Valido() {
        String generado = codigoService.generarEAN13Valido();
        Map<String, String> map = new HashMap<>();
        map.put("codigo", generado);
        return map;
    }

    @GetMapping("/generar-upc")
    public ResponseEntity<Map<String, String>> generarUPC() {
        String base11 = new Random().ints(11, 0, 10)
                .mapToObj(String::valueOf)
                .reduce("", String::concat);
        String codigo = codigoService.completarConChecksumUPCA(base11);
        return ResponseEntity.ok(Map.of("codigo", codigo));
    }
}
