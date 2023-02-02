package net.artux.pda.model.user;

import java.io.Serializable;
import java.time.Instant;
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
    private int pdaId;
    private Gang gang;
    private int xp;
    private GangRelation relations;

    private Instant registration;
    private Instant lastLoginAt;

}
