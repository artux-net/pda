package net.artux.pda.ui.fragments.rating;

import net.artux.pda.model.user.Gang;

import java.time.Instant;
import java.util.UUID;


public class UserInfo {

    public UUID id;
    public String login;
    public int pdaId;
    public Gang gang;
    public String avatar;
    public int xp;
    public Instant registration;
}
