package net.artux.pda.map.models;

import net.artux.pda.map.models.user.Gang;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UserGdx {


    private String login;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private String avatar;
    private int pdaId;
    private int admin;
    private int blocked;
    private int group;
    private int xp;
    private int money;
    private GangRelation relations;
    public List<Integer> achievements = new ArrayList<>();
    private Long lastModified;
    private Long registration;

    public UserGdx() {
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

    public void setMoney(int money) {
        this.money = money;
    }

    public List<Integer> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Integer> achievements) {
        this.achievements = achievements;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getRegistration() {
        return registration;
    }

    public void setRegistration(Long registration) {
        this.registration = registration;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRelation(Gang ofGang) {
        return getRelation(ofGang.getId());
    }

    public int getRelation(int groupId) {
        Class<GangRelation> z = GangRelation.class;
        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<>();
        for (Field f : z.getDeclaredFields()) {
            Gang gang = Gang.valueOf(f.getName().toUpperCase());
            f.setAccessible(true);
            try {
                result.put(gang.getId(), (Integer) f.get(relations));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result.get(groupId);
    }

    public GangRelation getRelations() {
        return relations;
    }

    public void setRelations(GangRelation relations) {
        this.relations = relations;
    }

    @Override
    public String toString() {
        return "Member{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
