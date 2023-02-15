package net.artux.pda.model.user;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class UserModel implements Serializable {

    private UUID id;
    private String login;
    private String email;
    private String name;
    private String nickname;
    private String avatar;
    private Role role;
    private Long pdaId;
    private Gang gang;
    private int xp;

    private Instant registration;
    private Instant lastLoginAt;

    public enum Role {
        ADMIN,
        USER,
        TESTER,
        MODERATOR
    }

}
