package net.artux.pdalib;

import net.artux.pdalib.profile.Data;
import net.artux.pdalib.profile.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Member {

    private String login;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private String avatar;
    private String token;
    private int pdaId;
    private int admin;
    private int blocked;
    private int group;
    private int xp;
    private int money;
    private String location;
    private Data data;
    public List<Integer> dialogs;
    public List<Integer> friends;
    public List<Integer> friendRequests;
    public List<Integer> relations;
    public List<Note> notes = new ArrayList<>();
    public List<Integer> achievements = new ArrayList<>();
    private Date lastModified;
    private String registrationDate;
    private Date lastLoginAt;

    public Member() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPdaId() {
        return pdaId;
    }

    public void setPdaId(int pdaId) {
        this.pdaId = pdaId;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getXp() {
        return xp;
    }

    public int getMoney() {
        return money;
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

    public List<Integer> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Integer> dialogs) {
        this.dialogs = dialogs;
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public List<Integer> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<Integer> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Integer> getRelations() {
        return relations;
    }

    public void setRelations(List<Integer> relations) {
        this.relations = relations;
    }

    @Override
    public String toString() {
        return "Member{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
