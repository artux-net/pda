package net.artux.pda.Models;

import com.google.gson.Gson;

import net.artux.pda.Models.profile.Data;

public class Member {

    private String login;
    private String name;
    private String email;
    private String password;
    private byte admin;
    private byte blocked;
    private int group;
    private String avatar;
    private int pdaId;
    private int xp;
    private String location;
    private String registrationDate;
    private String data;
    private String dialogs;


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

    public byte getAdmin() {
        return admin;
    }

    public byte getBlocked() {
        return blocked;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int groupId) {
        this.group = groupId;
    }

    public String getAvatarId() {
        return avatar;
    }

    public void setAvatarId(String avatar) {
        this.avatar = avatar;
    }

    public int getPdaId() {
        return pdaId;
    }

    public void setPdaId(int pdaId) {
        this.pdaId = pdaId;
    }

    public String getDialogs() {
        return dialogs;
    }

    public void setDialogs(String dialogs) {
        this.dialogs = dialogs;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Data getData() {
        return new Gson().fromJson(data, Data.class);
    }

    public void updateData(Data data) {
        this.data = new Gson().toJson(data);
    }

    public String toJson(){return new Gson().toJson(this);}
}
