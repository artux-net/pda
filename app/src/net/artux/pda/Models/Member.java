package net.artux.pda.Models;

import com.google.gson.Gson;

import net.artux.pda.Models.profile.Data;

import java.util.List;

public class Member {

    private String login;
    private String email;
    private String name;
    private String avatar;
    private String token;
    private int pdaId;
    private byte admin;
    private byte blocked;
    private int group;
    private int xp;
    private String location;
    private String data;
    private List<Integer> dialogs;
    private List<Integer> friends;
    private List<Integer> friendRequests;
    private String lastModified;
    private String registrationDate;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
