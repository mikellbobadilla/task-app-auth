package ar.mikellbobadilla.dto;

public record TaskRequest(
        String title,
        String description,
        boolean isDone,
        String targetDate
) {

}
