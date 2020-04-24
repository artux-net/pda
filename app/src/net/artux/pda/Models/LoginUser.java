package net.artux.pda.Models;

public class LoginUser {

    private String emailOrLogin;
    private String password;

    public LoginUser(String emailOrLogin, String password) {
        this.emailOrLogin = emailOrLogin;
        this.password = password;
    }

    public String getEmailOrLogin() {
        return emailOrLogin;
    }

    public String getPassword() {
        return password;
    }
}
