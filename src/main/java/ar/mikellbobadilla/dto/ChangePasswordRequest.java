package ar.mikellbobadilla.dto;

public record ChangePasswordRequest(
                String password,
                String newPassword,
                String confirmNewPassword) {
}
