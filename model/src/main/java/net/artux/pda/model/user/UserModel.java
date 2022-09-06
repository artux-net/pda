package net.artux.pda.model.user;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class UserModel implements Serializable {

    private UUID id;
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
    private Gang gang;
    private int xp;
    private int money;
    private GangRelation relations;
    public List<Integer> achievements = new ArrayList<>();
    private Instant lastModified;
    private Instant registration;
    private Instant lastLoginAt;

}
