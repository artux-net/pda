package net.artux.pda.model.user;

import lombok.Data;

@Data
public class LoginUser {

    private final String emailOrLogin;
    private final String password;

    public LoginUser(String emailOrLogin, String password) {
        this.emailOrLogin = emailOrLogin;
        this.password = password;
    }

}
