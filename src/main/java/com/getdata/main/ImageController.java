package com.getdata.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

  private static final String UPLOAD_DIR = "/data/images";

  @PostMapping("/uploadImage")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
      // Crea la ruta del archivo
      String fileName = file.getOriginalFilename();
      Path filePath = Paths.get(UPLOAD_DIR, fileName);

      // Guarda el archivo en el servidor
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      return ResponseEntity.ok("Archivo subido correctamente");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo");
    }
  }

  @GetMapping("/downloadImage/{fileName}")
  public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
    try {
      // Crea la ruta del archivo
      Path filePath = Paths.get(UPLOAD_DIR, fileName);

      // Crea el recurso del archivo
      Resource resource = new UrlResource(filePath.toUri());

      // Comprueba si el archivo existe
      if (resource.exists()) {
        // Configura la respuesta HTTP con el contenido del archivo
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .body(resource);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
