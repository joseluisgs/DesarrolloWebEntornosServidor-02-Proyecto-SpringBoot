package dev.joseluisgs.tiendaapidaw.rest.storage.controllers;

import dev.joseluisgs.tiendaapidaw.rest.storage.services.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
// Es la ruta del controlador, no le pongas nada mas para que genere la ruta bien
@RequestMapping("/storage")
// @PreAuthorize("hasRole('USER')") // OJO, si tenemos la pagina web, no podemos poner esto (no lo vemos!)
public class StorageController {
    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Obtiene un fichero del sistema de almacenamiento
     *
     * @param filename Nombre del fichero a obtener
     * @return Fichero
     */
    @GetMapping(value = "{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = storageService.loadAsResource(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("No se puede determinar el tipo de contenido del fichero");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

   /* @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // Aunque no es obligatorio, podemos indicar que se consume multipart/form-data
    // Para ficheros usamos, Resuqest part, porque lo tenemos dividido en partes
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestPart("file") MultipartFile file) {

        // Almacenamos el fichero y obtenemos su URL
        String urlImagen = null;

        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            urlImagen = storageService.getUrl(imagen);
            Map<String, Object> response = Map.of("url", urlImagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede subir un fichero vacío");
        }
    }*/

    // Implementar el resto de metodos del servicio que nos interesen...
    // Delete file, listar ficheros, etc....
}