package ar.mikellbobadilla.dto;

public record UpdatePasswordRequest(
        String password,
        String newPassword,
        String confirmNewPassword) {
}
