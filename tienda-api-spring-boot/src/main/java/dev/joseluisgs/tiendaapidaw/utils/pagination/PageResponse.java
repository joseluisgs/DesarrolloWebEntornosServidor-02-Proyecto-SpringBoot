package dev.joseluisgs.tiendaapidaw.utils.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber,
        int totalPageElements,
        boolean empty,
        boolean first,
        boolean last,
        String sortBy,
        String direction
) {
    // Podemos hacer un mapper en este caso
    public static <T> PageResponse<T> of(Page<T> page, String sortBy, String direction) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                sortBy,
                direction
        );
    }
}