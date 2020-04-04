package net.artux.pda.Models;

public class Profile {

    private String login;
    private String name;
    private byte admin;
    private byte blocked;
    private int group;
    private int avatar;
    private int pdaId;
    private int rang;
    private String location;
    private int achievements;
    private String registrationDate;

    Profile(String login, String name, byte admin, byte blocked,
            int group, int avatar, int pdaId, int rang, String location,
            int achievements, String registrationDate, String profileJson) {
        this.login = login;
        this.name = name;
        this.admin = admin;
        this.blocked = blocked;
        this.group = group;
        this.avatar = avatar;
        this.pdaId = pdaId;
        this.rang = rang;
        this.location = location;
        this.achievements = achievements;
        this.registrationDate = registrationDate;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public byte getAdmin() {
        return admin;
    }

    public byte getBlocked() {
        return blocked;
    }

    public int getGroup() {
        return group;
    }

    public int getAvatar() {
        return avatar;
    }

    public int getPdaId() {
        return pdaId;
    }

    public int getRang() {
        return rang;
    }

    public String getLocation() {
        return location;
    }

    public int getAchievements() {
        return achievements;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
}