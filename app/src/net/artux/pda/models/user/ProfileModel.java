package net.artux.pda.models.user;

public class ProfileModel {

    private String login;
    private String name;
    private String avatar;
    private int pdaId;
    private int xp;
    private Long registration;
    private FriendRelation friendRelation;
    private Gang gang;
    private GangRelation relations;

    private int achievements;
    private int group;

    private int friendStatus;
    /*
    0 - is not friend
    1 - friend
    2 - subscriber
    3 - requested
     */
    private int friends;
    private int subs;

    public ProfileModel() {
    }

    public ProfileModel(UserModel userModel) {
        this.login = userModel.getLogin();
        this.name = userModel.getName();
        this.group = userModel.getGang().getId();
        this.avatar = userModel.getAvatar();
        this.pdaId = userModel.getPdaId();
        this.xp = userModel.getXp();
        this.registration = userModel.getRegistration();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public Long getRegistration() {
        return registration;
    }

    public void setRegistration(Long registration) {
        this.registration = registration;
    }

    public FriendRelation getFriendRelation() {
        return friendRelation;
    }

    public void setFriendRelation(FriendRelation friendRelation) {
        this.friendRelation = friendRelation;
    }

    public Gang getGang() {
        return gang;
    }

    public void setGang(Gang gang) {
        this.gang = gang;
    }

    public GangRelation getRelations() {
        return relations;
    }

    public void setRelations(GangRelation relations) {
        this.relations = relations;
    }

    public int getAchievements() {
        return achievements;
    }

    public void setAchievements(int achievements) {
        this.achievements = achievements;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getSubs() {
        return subs;
    }

    public void setSubs(int subs) {
        this.subs = subs;
    }
}