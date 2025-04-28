# Detector de Información Sensible en Imágenes

Este proyecto proporciona un backend en Java (Spring Boot) que analiza imágenes para extraer texto (usando Azure Computer Vision OCR) y detecta información personal sensible (DNI, placas, direcciones) mediante lógica por expresiones regulares y, opcionalmente, Azure OpenAI para detección avanzada.

## Tabla de Contenidos

1. [Resumen](#resumen)
2. [Requisitos](#requisitos)
3. [Configuración](#configuración)
4. [Compilación y Ejecución](#compilación-y-ejecución)
5. [API REST](#api-rest)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Dependencias Principales](#dependencias-principales)
8. [Despliegue en Azure](#despliegue-en-azure)
9. [Pruebas y Depuración](#pruebas-y-depuración)
10. [Licencia](#licencia)

---

## Resumen

Al enviar una imagen (JPG/PNG) al endpoint `/api/analyze`, la aplicación:

1. Extrae texto de la imagen usando Azure OCR.
2. Detecta patrones sensibles con expresiones regulares.
3. Si está configurado, envía el texto a Azure OpenAI Chat Completions para detección avanzada.
4. Guarda cada detección (tipo, valor, timestamp y hash SHA-256 de la imagen) en la base de datos.
5. Devuelve al cliente un JSON con las detecciones encontradas.

---

## Requisitos

- **Java**: JDK 17 o superior (verificar con `java -version`).
- **Maven**: 3.6+ (`mvn -v`).
- **Cuenta Azure** con los servicios:
  - Computer Vision (OCR)
  - OpenAI (Chat Completions) – opcional
- **Variables de entorno** antes de ejecutar:
  - `AZURE_OCR_ENDPOINT`  (p. ej. `https://<tu-recurso>.cognitiveservices.azure.com/vision/v4.0/ocr`)
  - `AZURE_OCR_KEY`
  - `AZURE_OPENAI_ENDPOINT`  (p. ej. `https://<tu-recurso>.openai.azure.com/`)
  - `AZURE_OPENAI_KEY`
  - `AZURE_OPENAI_DEPLOYMENT`  (p. ej. `gpt-35-turbo`)

---

## Configuración

1. Clonar el repositorio:
   ```bash
   git clone <URL-del-repo>
   cd HACKATHON
   ```

2. Crear o editar `src/main/resources/application.properties` con:
   ```properties
   # Base de datos en memoria (desarrollo)
   spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Azure Computer Vision OCR
   azure.ocr.endpoint=${AZURE_OCR_ENDPOINT}
   azure.ocr.key=${AZURE_OCR_KEY}

   # Azure OpenAI Chat Completions (opcional)
   azure.openai.endpoint=${AZURE_OPENAI_ENDPOINT}
   azure.openai.key=${AZURE_OPENAI_KEY}
   azure.openai.deployment-name=${AZURE_OPENAI_DEPLOYMENT}
   ```

3. Definir variables de entorno:
   - **Linux/macOS**:
     ```bash
     export AZURE_OCR_ENDPOINT="https://<tu-recurso>.cognitiveservices.azure.com/vision/v4.0/ocr"
     export AZURE_OCR_KEY="<tu-clave>"
     export AZURE_OPENAI_ENDPOINT="https://<tu-recurso>.openai.azure.com/"
     export AZURE_OPENAI_KEY="<tu-clave>"
     export AZURE_OPENAI_DEPLOYMENT="gpt-35-turbo"
     ```
   - **Windows PowerShell**:
     ```powershell
     $Env:AZURE_OCR_ENDPOINT = "https://<tu-recurso>.cognitiveservices.azure.com/vision/v4.0/ocr"
     $Env:AZURE_OCR_KEY      = "<tu-clave>"
     $Env:AZURE_OPENAI_ENDPOINT = "https://<tu-recurso>.openai.azure.com/"
     $Env:AZURE_OPENAI_KEY      = "<tu-clave>"
     $Env:AZURE_OPENAI_DEPLOYMENT = "gpt-35-turbo"
     ```

4. (Opcional) Para producción con Azure SQL, descomentar y ajustar en `application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://<servidor>.database.windows.net:1433;database=<db>
   spring.datasource.username=<usuario>@<servidor>
   spring.datasource.password=<password>
   spring.jpa.hibernate.ddl-auto=update
   ```

---

## Compilación y Ejecución

1. **Compilar con Maven**:
   ```bash
   mvn clean package
   ```

2. **Ejecutar en modo desarrollo**:
   ```bash
   mvn spring-boot:run
   ```
   La aplicación escuchará en `http://localhost:8080`.

3. **Ejecutar JAR**:
   ```bash
   java -jar target/image-protection-0.0.1-SNAPSHOT.jar
   ```

**En IDE**: configurar Run/Debug con las variables de entorno listadas para no incluirlas en código.

---

## API REST

### POST /api/analyze

- **Descripción**: recibe un archivo `multipart/form-data` y devuelve detecciones.
- **Request**:
  - `Content-Type`: `multipart/form-data`
  - Campo `file`: imagen JPG/PNG

- **Response 200**: JSON array de objetos:
  ```json
  [
    { "tipoDato": "dni", "valor": "87654321" },
    { "tipoDato": "placa", "valor": "ABC123" }
  ]
  ```

- **Response 500**:
  ```json
  { "error": "No se pudo procesar la imagen" }
  ```

- **Ejemplo cURL**:
  ```bash
  curl -X POST http://localhost:8080/api/analyze \
    -H "Content-Type: multipart/form-data" \
    -F "file=@./ejemplo.jpg"
  ```

- **Postman**: importar colección `postman-collection.json` (si existe).

---

## Estructura del Proyecto

```
HACKATHON/
├─ pom.xml                                # Dependencias y plugin Maven
├─ README.md                              # Documentación
└─ src/main/java/com/example/imageprotection/
   ├─ ImageProtectionApplication.java     # Clase principal de arranque
   ├─ config/
   │   ├─ AppConfig.java                  # Bean RestTemplate
   │   ├─ AzureOcrConfig.java             # @ConfigurationProperties OCR
   │   └─ AzureOpenAIConfig.java          # @ConfigurationProperties OpenAI
   ├─ controller/
   │   └─ ImageAnalysisController.java    # Endpoint `/api/analyze`
   ├─ service/
   │   ├─ AzureOcrService.java            # Llamada HTTP a OCR
   │   ├─ AzureOpenAIService.java         # Chat Completions
   │   └─ SensitiveDataDetector.java      # Regex fallback
   ├─ model/
   │   ├─ Detection.java                  # DTO respuesta
   │   └─ DetectionRecord.java            # Entidad JPA
   └─ repository/
       └─ DetectionRecordRepository.java  # Interfaz JPA
```

---

## Dependencias Principales

- **Spring Boot Starter Web**
- **Spring Data JPA**
- **H2 Database** (desarrollo)
- **Azure SDK:**
  - `azure-ai-openai` (v1.0.0-beta.16)
  - `azure-core` (v1.52.0)
  - `azure-core-http-netty` (v1.15.0)
- **Jackson Databind**

---

## Despliegue en Azure

1. Crear un App Service en Azure (Linux/Windows según preferencia).
2. En **Configuration > Application Settings**, añadir:
   - `AZURE_OCR_ENDPOINT`, `AZURE_OCR_KEY`, `AZURE_OPENAI_ENDPOINT`, `AZURE_OPENAI_KEY`, `AZURE_OPENAI_DEPLOYMENT`.
3. (Opcional) configurar cadena de Azure SQL en `application.properties`.
4. Desplegar usando Maven Plugin:
   ```xml
   <plugin>
     <groupId>com.microsoft.azure</groupId>
     <artifactId>azure-webapp-maven-plugin</artifactId>
     <version>1.14.0</version>
     <configuration>
       <resourceGroup>mi-rg</resourceGroup>
       <appName>image-protection-app</appName>
     </configuration>
   </plugin>
   ```
   ```bash
   mvn azure-webapp:deploy
   ```

---

## Pruebas y Depuración

- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL predeterminado: `jdbc:h2:mem:testdb`).
- **Logs**: nivel `INFO`/`DEBUG` muestra flujo de OCR y detecciones.
- **Postman**: verificar endpoint `/api/analyze`.

---

## Licencia

Este proyecto está licenciado bajo MIT. Véase el archivo `LICENSE` para más detalles.
