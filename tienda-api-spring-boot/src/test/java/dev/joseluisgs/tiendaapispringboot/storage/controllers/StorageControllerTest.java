package dev.joseluisgs.tiendaapispringboot.storage.controllers;

import dev.joseluisgs.tiendaapispringboot.rest.storage.services.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class StorageControllerTest {

    private final String apiVersion = "/v1"; // Asegúrate de que este valor coincide con tu configuración
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StorageService storageService;

    @Test
    public void testServeFile() throws Exception {
        // Preparar el mock del servicio de almacenamiento
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Contenido del archivo".getBytes());

        // Simular el comportamiento del servicio de almacenamiento
        when(storageService.loadAsResource(any())).thenReturn(mockFile.getResource());


        // Realizar la solicitud GET al controlador
        MockHttpServletResponse response = mockMvc.perform(
                        get(apiVersion + "/storage/" + mockFile.getName()))
                .andReturn().getResponse();

        // Verificar la respuesta
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                //() -> assertEquals(MediaType.TEXT_PLAIN_VALUE, response.getContentType()),
                () -> assertEquals("Contenido del archivo", response.getContentAsString())
        );
    }
}