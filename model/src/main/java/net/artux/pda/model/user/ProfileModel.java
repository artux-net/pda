package net.artux.pda.model.user;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class ProfileModel {

    private UUID id;
    private String login;
    private String name;
    private String nickname;
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

    public Rang getRang() {
        return getRang(xp);
    }

    public static Rang getRang(int xp) {
        Rang previousRang = Rang.BEGINNER;
        for (Rang rang : Rang.values()) {
            if (rang.getXp() > xp)
                return previousRang;
            else previousRang = rang;
        }
        return Rang.FINAL;
    }

    public enum Rang {
        BEGINNER(0, 0),
        NEW(1, 1000),
        STALKER(2, 3000),
        EXPERIENCE(3, 6000),
        OLD(4, 1000),
        MASTER(5, 16000),
        FINAL(6, Integer.MAX_VALUE, true);

        private final int id;
        private final int xp;
        private final boolean last;

        Rang(int id, int xp) {
            this(id, xp, false);
        }

        Rang(int id, int xp, boolean last) {
            this.id = id;
            this.xp = xp;
            this.last = last;
        }

        public int getId() {
            return id;
        }

        public boolean isLast() {
            return last;
        }

        public int getXp() {
            return xp;
        }

        public Rang getNextRang() {
            if (id < Rang.values().length - 1)
                return Rang.values()[id + 1];
            else return null;
        }
    }
}