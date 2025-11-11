package dev.joseluisgs.tiendaapidaw.rest.pedidos.services;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions.*;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.LineaPedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.repositories.PedidosRepository;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapidaw.rest.productos.repositories.ProductosRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidosServiceImplTest {
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private ProductosRepository productosRepository;

    @InjectMocks
    private PedidosServiceImpl pedidosService;

    @Test
    void findAll_ReturnsPageOfPedidos() {
        // Arrange
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);

        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Pedido> result = pedidosService.findAll(pageable);

        // Assert
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );

        // Verify
        verify(pedidosRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido expectedPedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(expectedPedido));

        // Act
        Pedido resultPedido = pedidosService.findById(idPedido);

        // Assert
        assertEquals(expectedPedido, resultPedido);

        // Verify
        verify(pedidosRepository).findById(idPedido);
    }

    @Test
    void testFindById_ThrowsPedidoNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));

        // Verify
        verify(pedidosRepository).findById(idPedido);
    }

    @Test
    void testFindByIdUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = mock(Pageable.class);
        @SuppressWarnings("unchecked")
        Page<Pedido> expectedPage = mock(Page.class);
        when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(expectedPage);

        // Act
        Page<Pedido> resultPage = pedidosService.findByIdUsuario(idUsuario, pageable);

        // Assert
        assertEquals(expectedPage, resultPage);

        // Verify
        verify(pedidosRepository).findByIdUsuario(idUsuario, pageable);
    }

    @Test
    void testSave() {
        // Arrange
        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();

        Pedido pedido = new Pedido();
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = new Pedido();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToSave); // Utiliza any(Pedido.class) para cualquier instancia de Pedido
        when(productosRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // Act
        Pedido resultPedido = pedidosService.save(pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToSave, resultPedido),
                () -> assertEquals(pedidoToSave.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );

        // Verify
        verify(pedidosRepository).save(any(Pedido.class));
        verify(productosRepository, times(2)).findById(anyLong());
    }

    @Test
    void testSave_ThrowsPedidoNotItems() {
        // Arrange
        Pedido pedido = new Pedido();

        // Act & Assert
        assertThrows(PedidoNotItems.class, () -> pedidosService.save(pedido));

        // Verify
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(productosRepository, never()).findById(anyLong());
    }

    @Test
    void testDelete() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelete = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelete));

        // Act
        pedidosService.delete(idPedido);

        // Assert


        // Verify
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).deleteById(idPedido);
    }

    @Test
    void testDelete_ThrowsPedidoNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.delete(idPedido));

        // Verify
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).deleteById(idPedido);
    }

    @Test
    void testUpdate() {
        // Arrange
        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();


        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();

        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToUpdate = new Pedido();
        pedidoToUpdate.setLineasPedido(List.of(lineaPedido)); // Inicializar la lista de líneas de pedido

        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToUpdate));
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToUpdate);
        when(productosRepository.findById(anyLong())).thenReturn(Optional.of(producto));

        // Act
        Pedido resultPedido = pedidosService.update(idPedido, pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToUpdate, resultPedido),
                () -> assertEquals(pedidoToUpdate.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToUpdate.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );

        // Verify
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).save(any(Pedido.class));
        verify(productosRepository, times(3)).findById(anyLong());
    }

    @Test
    void testUpdate_ThrowsPedidoNotFound() {
        // Arrange
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PedidoNotFound.class, () -> pedidosService.update(idPedido, pedido));

        // Verify
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(productosRepository, never()).findById(anyLong());
    }

    @Test
    void testReserveStockPedidos() throws PedidoNotFound, ProductoNotFound, ProductoBadPrice {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();

        lineasPedido.add(lineaPedido1); // Agregar la línea de pedido a la lista

        pedido.setLineasPedido(lineasPedido); // Asignar la lista de líneas de pedido al pedido

        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();

        when(productosRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Pedido result = pedidosService.reserveStockPedidos(pedido);

        // Assert
        assertAll(
                () -> assertEquals(3, producto.getStock()), // Verifica que el stock se haya actualizado correctamente
                () -> assertEquals(20.0, lineaPedido1.getTotal()), // Verifica que el total de la línea de pedido se haya calculado correctamente
                () -> assertEquals(20.0, result.getTotal()), // Verifica que el total del pedido se haya calculado correctamente
                () -> assertEquals(2, result.getTotalItems()) // Verifica que el total de items del pedido se haya calculado correctamente
        );

        // Verify
        verify(productosRepository, times(1)).findById(1L);
        verify(productosRepository, times(1)).save(producto);
    }

    @Test
    void returnStockPedidos_ShouldReturnPedidoWithUpdatedStock() {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .build();

        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Producto producto = Producto.builder()
                .id(1L)
                .stock(13)
                .build();

        when(productosRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productosRepository.save(producto)).thenReturn(producto);

        // Act
        Pedido result = pedidosService.returnStockPedidos(pedido);

        // Assert
        assertEquals(15, producto.getStock());
        assertEquals(pedido, result);

        // Verify
        verify(productosRepository, times(1)).findById(1L);
        verify(productosRepository, times(1)).save(producto);
    }

    @Test
    void checkPedido_ProductosExistenYHayStock_NoDebeLanzarExcepciones() {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();

        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();

        when(productosRepository.findById(1L)).thenReturn(Optional.of(producto));


        // Act & Assert
        assertDoesNotThrow(() -> pedidosService.checkPedido(pedido));

        // Verify
        verify(productosRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedido_ProductoNoExiste_DebeLanzarProductoNotFound() {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();

        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        when(productosRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductoNotFound.class, () -> pedidosService.checkPedido(pedido));

        // Verify
        verify(productosRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedido_ProductoNoTieneSuficienteStock_DebeLanzarProductoNotStock() {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();
        lineaPedido1.setIdProducto(1L);
        lineaPedido1.setCantidad(10);
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();

        when(productosRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        assertThrows(ProductoNotStock.class, () -> pedidosService.checkPedido(pedido));

        // Verify
        verify(productosRepository, times(1)).findById(1L);
    }

    @Test
    void checkPedido_PrecioProductoDiferente_DebeLanzarProductoBadPrice() {
        // Arrange
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(10.0)
                .build();
        lineaPedido1.setIdProducto(1L);
        lineaPedido1.setCantidad(2);
        lineaPedido1.setPrecioProducto(20.0); // Precio diferente al del producto
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);

        Producto producto = Producto.builder()
                .id(1L)
                .stock(5)
                .precio(10.0)
                .build();

        when(productosRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        assertThrows(ProductoBadPrice.class, () -> pedidosService.checkPedido(pedido));

        // Verify
        verify(productosRepository, times(1)).findById(1L);
    }

}