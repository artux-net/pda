package net.artux.pdalib;

import java.util.List;

public class Profile {

    private String login;
    private String name;
    private int admin;
    private int blocked;
    private int group;
    private String avatar;
    private int pdaId;
    private int xp;
    private String location;
    private Long registration;
    private int friendStatus;
    /*
    0 - is not friend
    1 - friend
    2 - subscriber
    3 - requested
     */
    private int friends;
    private int subs;
    private List<Integer> relations;

    //TODO
    private int achievements;

    public Profile() {
    }

    public Profile(Member member) {
        this.login = member.getLogin();
        this.name = member.getName();
        this.admin = member.getAdmin();
        this.blocked = member.getBlocked();
        this.group = member.getGroup();
        this.avatar = member.getAvatar();
        this.pdaId = member.getPdaId();
        this.xp = member.getXp();
        this.location = member.getLocation();
        this.registration = member.getRegistration();
        this.friends = member.getFriends().size();
        this.subs = member.getRequests().size();
        this.relations = member.getRelations();
    }

    public Profile(Member member, Member by) {
        this.login = member.getLogin();
        this.name = member.getName();
        this.admin = member.getAdmin();
        this.blocked = member.getBlocked();
        this.group = member.getGroup();
        this.avatar = member.getAvatar();
        this.pdaId = member.getPdaId();
        this.xp = member.getXp();
        this.location = member.getLocation();
        this.registration = member.getRegistration();
        this.friends = member.getFriends().size();
        this.subs = member.getRequests().size();
        this.relations = member.getRelations();

        setFriendStatus(member, by);
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public int getAdmin() {
        return admin;
    }

    public int getBlocked() {
        return blocked;
    }

    public int getGroup() {
        return group;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPdaId() {
        return pdaId;
    }

    public int getXp() {
        return xp;
    }

    public String getLocation() {
        return location;
    }

    public Long getRegistration() {
        return registration;
    }

    public int getFriends() {
        return friends;
    }

    public int getSubs() {
        return subs;
    }

    public List<Integer> getRelations() {
        return relations;
    }

    private void setFriendStatus(Member member, Member by) {
        if (member.getRequests().contains(by.getPdaId())) {
            friendStatus = 3;
        } else if (by.getRequests().contains(pdaId)) {
            friendStatus = 2;
        } else if (member.getFriends().contains(by.getPdaId())) {
            friendStatus = 1;
        } else {
            friendStatus = 0;
        }
    }

    public int getFriendStatus() {
        return friendStatus;
    }
}