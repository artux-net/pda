package net.artux.pda.Models;

public class LoginStatus {

    private boolean success;
    private int code;
    private String description;
    private String token;

    public LoginStatus(boolean success, int code, String description, String token) {
        this.success = success;
        this.code = code;
        this.description = description;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
