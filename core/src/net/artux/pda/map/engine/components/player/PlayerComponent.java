package net.artux.pda.map.engine.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;

import net.artux.pdalib.Member;

public class PlayerComponent implements Component {

    public Camera camera;
    public Member member;

    public PlayerComponent(Camera camera, Member member) {
        this.camera = camera;
        this.member = member;
    }
}