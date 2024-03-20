package ar.mikellbobadilla.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
}
