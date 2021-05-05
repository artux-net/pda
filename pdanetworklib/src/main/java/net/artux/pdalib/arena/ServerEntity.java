package net.artux.pdalib.arena;

import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;

public class ServerEntity {

    final static double MOVEMENT = 0.4;

    private final Position position;
    private final Profile profile;

    public ServerEntity(Position position, Member profile) {
        this.position = position;
        this.profile = new Profile(profile);
    }

    public Position getPosition() {
        return position;
    }

    public void move(Position position){
        this.position.moveBy(position.x*MOVEMENT, position.y*MOVEMENT);
    }

}
