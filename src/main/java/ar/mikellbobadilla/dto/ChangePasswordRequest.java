package ar.mikellbobadilla.dto;

public record ChangePasswordRequest(
        String password,
        String newPassword,
        String confirmNewPassword) {

    public ChangePasswordRequest withNewPasswordAndConfirmNewPassword(String newPassword, String confirmNewPassword) {
        return new ChangePasswordRequest(password, newPassword, confirmNewPassword);
    }

    public ChangePasswordRequest withPassword(String password) {
        return new ChangePasswordRequest(password, newPassword, confirmNewPassword);
    }
}
