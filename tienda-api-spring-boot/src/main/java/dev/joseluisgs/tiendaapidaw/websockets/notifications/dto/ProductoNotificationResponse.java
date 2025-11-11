package dev.joseluisgs.tiendaapidaw.websockets.notifications.dto;

public record ProductoNotificationResponse(
        Long id,
        String marca,
        String modelo,
        String descripcion,
        Double precio,
        String imagen,
        Integer stock,
        String categoria,
        String uuid,
        Boolean isDeleted,
        String createdAt,
        String updatedAt
) {
}