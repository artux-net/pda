package net.artux.pda.model.user;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class ProfileModel {

    private UUID id;
    private String login;
    private String name;
    private String avatar;
    private int pdaId;
    private int xp;
    private Instant registration;
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

}