package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

public class SoundComponent implements Component {

    public Sound sound;
    public float defaultVolume;
    public Playable playable;

    public SoundComponent(Sound sound, float defaultVolume, Playable playable) {
        this.sound = sound;
        this.defaultVolume = defaultVolume;
        this.playable = playable;
    }

    interface Playable{
        void play();
    }
}