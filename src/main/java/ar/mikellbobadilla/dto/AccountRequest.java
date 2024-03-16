package ar.mikellbobadilla.dto;

public record AccountRequest(
        String username,
        String password,
        String secondPassword) {
}
