package net.artux.pdalib.arena;

import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;

public class ServerEntity {

    public final int pdaId;
    private final Position position;
    private final Position velocity;


    public ServerEntity(Position position, Member profile) {
        this.position = position;
        velocity = new Position(0,0);
        this.pdaId = profile.getPdaId();
    }

    public Position getPosition() {
        return position;
    }

    public Position getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "ServerEntity{" +
                "pdaId=" + pdaId +
                ", position=" + position +
                ", velocity=" + velocity +
                '}';
    }
}
