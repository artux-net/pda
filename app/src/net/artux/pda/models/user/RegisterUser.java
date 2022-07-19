package net.artux.pda.models.user;

public class RegisterUser {

    private String login;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String avatar;

    public RegisterUser(String login, String name, String nickname, String email, String password, int avatarId) {
        this.login = login;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.avatar = String.valueOf(avatarId);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarId() {
        return avatar;
    }

    public void setAvatarId(int avatarId) {
        this.avatar = avatar;
    }
}
