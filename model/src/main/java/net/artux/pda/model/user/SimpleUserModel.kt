package net.artux.pda.model.user;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class SimpleUserModel {

    private UUID id;
    private String login;
    private String nickname;
    private String avatar;
    private int pdaId;
    private int xp;
    private int achievements;
    private Gang gang;
    private Instant registration;
}
