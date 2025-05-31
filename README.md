# EasyCode
# 🔢 Generador de Códigos QR y de Barras

Este proyecto permite generar diferentes tipos de **códigos de barras** y **códigos QR** desde una interfaz web sencilla. Fue desarrollado en **Java 21** usando **Spring Boot 3.4.4**, con **HTML, CSS y JavaScript** en el frontend.

## 🧩 Tipos de códigos soportados

- QR (`QR_CODE`)
- Code 128 (`CODE_128`)
- EAN-13 (`EAN_13`)
- UPC-A (`UPC_A`)
- Code 39 (`CODE_39`)
- PDF417 (`PDF_417`)

## 🧪 Tecnologías utilizadas

- Java 21
- Spring Boot (Web, Thymeleaf, Validation)
- ZXing (QR y barras)
- Barcode4J (formatos extendidos)
- OpenPDF (descarga en PDF)
- HTML, CSS y JS

## 🎯 Funcionalidades principales

- ✅ Selección del tipo de código a generar
- ✏️ Ingreso de datos personalizados
- 📷 Vista previa de la imagen generada
- 💾 Opción para **descargar** la imagen o **imprimir**
- 📄 Generación de un **PDF** con el código incluido (opcional)

## 📁 Estructura del proyecto

src/
├── main/
│ ├── java/com/generadordecodigo/
│ │ ├── controller/ # Controladores web
│ │ ├── model/ # Clases de dominio
│ │ ├── service/ # Lógica de negocio
│ │ └── GeneradoCodigosApplication.java
│ └── resources/
│ ├── static/ # Archivos estáticos (CSS, JS)
│ ├── templates/ # HTML con Thymeleaf
│ └── application.properties


