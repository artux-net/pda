package net.artux.pda.model.user;

import lombok.Data;

@Data
public class RegisterUserModel {

    private String login;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String avatar;

    public RegisterUserModel(String login, String name, String nickname, String email, String password, String avatarId) {
        this.login = login;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.avatar = avatarId;
    }

}
