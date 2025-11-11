package dev.joseluisgs.tiendaapidaw.rest.storage.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


/**
 * Esta interfaz nos permite definir una abstracción de lo que debería
 * ser un almacén secundario de información, de forma que podamos usarlo
 * en un controlador.
 * <p>
 * De esta forma, vamos a poder utilizar un almacén que acceda a nuestro
 * sistema de ficheros, o también podríamos implementar otro que estuviera
 * en un sistema remoto, almacenar los ficheros en un sistema GridFS, ...
 *
 * @author Equipo de desarrollo de Spring
 */
public interface StorageService {

    // Inicia sl sistema de ficheros
    void init();

    /**
     * Almacena un fichero en el sistema de ficheros
     *
     * @param file fichero a almacenar
     * @return nombre del fichero almacenado
     */
    String store(MultipartFile file);

    /**
     * Devuelve un Stream de todos los ficheros almacenados
     *
     * @return Stream de Paths
     */
    Stream<Path> loadAll();

    /**
     * Devuelve el Path del fichero
     *
     * @param filename nombre del fichero
     * @return Path del fichero
     */
    Path load(String filename);

    /**
     * Devuelve un recurso del fichero
     *
     * @param filename nombre del fichero
     * @return Recurso del fichero
     */
    Resource loadAsResource(String filename);

    /**
     * Borra un fichero del sistema de ficheros
     *
     * @param filename nombre del fichero
     */
    void delete(String filename);

    /**
     * Borra todos los ficheros del sistema de ficheros
     */
    void deleteAll();

    /**
     * Devuelve la URL del fichero
     *
     * @param filename nombre del fichero
     * @return URL del fichero
     */
    String getUrl(String filename);

}