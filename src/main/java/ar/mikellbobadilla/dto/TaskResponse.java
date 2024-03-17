package ar.mikellbobadilla.dto;

public record TaskResponse(
    Long id,
    String title,
    String description,
    boolean isDone,
    String targetDate
) {
    
}
