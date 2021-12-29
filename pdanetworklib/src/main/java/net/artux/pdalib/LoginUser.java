package net.artux.pdalib;

public class LoginUser {

    private final String emailOrLogin;
    private final String password;

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
