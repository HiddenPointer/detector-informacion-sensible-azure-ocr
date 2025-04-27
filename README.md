# Proyecto: Agente IA de Protección de Imagen Personal

Este repositorio contiene el backend de una aplicación que analiza imágenes para detectar información personal sensible (DNI, placas, direcciones) y alerta al usuario antes de que publiquen material comprometedor.

---

## 1. Descripción general

- **Tecnologías**: Java 17, Spring Boot 3.x, Spring Web, Spring Data JPA, H2 (desarrollo), Azure SQL (producción), Azure Computer Vision (OCR).  
- **Arquitectura**:  
  - **Frontend SPA** (no incluido aquí) se comunica vía API REST.  
  - **Backend Spring Boot** recibe imágenes, invoca OCR y ejecuta lógica de detección.  
  - **Base de datos** almacena cada detección: tipo, valor, timestamp y hash de imagen.

---

## 2. Requisitos previos

1. **Java 17** instalado y configurado en `JAVA_HOME`.  
2. **Maven** instalado y en `PATH`.  
3. Credenciales de Azure para OCR:  
   - `AZURE_OCR_ENDPOINT` (URL de tu recurso Computer Vision).  
   - `AZURE_OCR_KEY` (clave de suscripción).  
4. (Opcional) **Azure SQL Database**: cadena de conexión si usas SQL en lugar de H2.

---

## 3. Configuración del proyecto

1. Clona este repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd HACKATHON
   ```
2. Copia y ajusta `src/main/resources/application.properties`:
   ```properties
   # H2 en memoria para dev
   spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Azure OCR (variables de entorno)
   azure.ocr.endpoint=${AZURE_OCR_ENDPOINT}
   azure.ocr.key=${AZURE_OCR_KEY}

   # Para usar Azure SQL (descomenta y ajusta):
   # spring.datasource.url=jdbc:sqlserver://<tu-servidor>.database.windows.net:1433;database=<tu-db>
   # spring.datasource.username=<usuario>@<tu-servidor>
   # spring.datasource.password=<tu-password>
   # spring.jpa.hibernate.ddl-auto=update
   ```
3. Exporta variables de entorno en tu terminal:
   ```bash
   export AZURE_OCR_ENDPOINT="https://<tu-recurso>.cognitiveservices.azure.com/vision/v4.0/ocr"
   export AZURE_OCR_KEY="<tu-clave>"
   ```
   En Windows PowerShell:
   ```powershell
   $Env:AZURE_OCR_ENDPOINT="https://..."
   $Env:AZURE_OCR_KEY="..."
   ```

---

## 4. Compilación y ejecución

### 4.1 Compilar con Maven

```bash
mvn clean package
```

### 4.2 Ejecutar en modo desarrollo

```bash
mvn spring-boot:run
```

la aplicación arrancará en `http://localhost:8080`.

### 4.3 Ejecutar JAR generado

```bash
java -jar target/image-protection-0.0.1-SNAPSHOT.jar
```

---

## 5. Uso de la API REST

### 5.1 Endpoint principal

`POST /api/analyze`  
- **Consumes**: `multipart/form-data`  
- **Parámetro**: `file` (imagen JPEG/PNG)  
- **Respuesta 200**: JSON array de objetos `{ "tipoDato": "dni", "valor": "87654321" }`.  
- **Respuesta 500**: `{ "error": "No se pudo procesar la imagen" }`

### 5.2 Ejemplo con cURL

```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: multipart/form-data" \
  -F "file=@./ejemplo_dni.jpg"
```

---

## 6. Estructura de código

```
HACKATHON/
├─ pom.xml                # Dependencias y build
├─ README.md              # Este documento
└─ src/main/java/
   └─ com/example/imageprotection/
      ├─ ImageProtectionApplication.java  # Clase main (arranque)
      ├─ config/
      │  ├─ AppConfig.java                # Bean RestTemplate
      │  └─ AzureOcrConfig.java           # Bind de propiedades OCR
      ├─ controller/
      │  └─ ImageAnalysisController.java  # Endpoint `/api/analyze`
      ├─ model/
      │  ├─ Detection.java                # DTO de respuesta
      │  └─ DetectionRecord.java          # Entidad JPA
      ├─ repository/
      │  └─ DetectionRecordRepository.java  # Repositorio JPA
      └─ service/
         ├─ AzureOcrService.java         # Llama a Azure OCR
         └─ SensitiveDataDetector.java    # Detección regex
```

---

## 7. Despliegue en Azure

1. Ajusta en `application.properties` la conexión a Azure SQL.  
2. Configura variables de entorno en App Service (AZURE_OCR_ENDPOINT, AZURE_OCR_KEY).  
3. Usa Azure Maven Plugin o GitHub Actions para desplegar JAR a App Service:
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

---

## 8. Pruebas y depuración

- **H2 Console**: accede a `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`).  
- **Logs**: la aplicación imprime SQL y flujo OCR/detección.  
- **Postman**: importar colección para `/api/analyze`.

---

## 9. Licencia

Este código está bajo licencia MIT. Puedes adaptarlo para tus proyectos.

---

> Para dudas o mejoras, abre un issue o contáctanos directamente.
