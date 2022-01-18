package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.artux.pdalib.Member;

public class PlayerComponent implements Component {

    public Camera camera;
    public Member member;

    public PlayerComponent(Camera camera, Member member) {
        this.camera = camera;
        this.member = member;
    }
}