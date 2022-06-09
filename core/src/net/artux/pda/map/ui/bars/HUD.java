package net.artux.pda.map.ui.bars;


import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.ui.TextureActor;
import net.artux.pda.map.ui.UserInterface;

public class HUD extends Table {

    private Bar healthBar;
    private Bar staminaBar;
    private Bar radiationBar;
    private PlayerSystem playerSystem;
    private Label distanceLabel;

    public HUD(AssetManager assetManager, Engine engine, UserInterface userInterface) {
        super();
        playerSystem = engine.getSystem(PlayerSystem.class);

        top();
        left();

        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(assetManager.get("ui/bar/hudBackground.png", Texture.class));
        setBackground(textureRegionDrawable);
        setSize(textureRegionDrawable.getRegion().getRegionWidth(), textureRegionDrawable.getRegion().getRegionHeight());
        defaults().pad(5);
        float iconSize = Gdx.graphics.getHeight() / 60f;
        TextureActor actor = new TextureActor(assetManager.get("ui/bar/ic_health.png", Texture.class));
        actor.setSize(iconSize, iconSize);
        add(actor);
        healthBar = new Bar(Color.RED);
        add(healthBar)
                .fillX()
                .expandX();

        actor = new TextureActor(assetManager.get("ui/bar/ic_stamina.png", Texture.class));
        actor.setSize(iconSize, iconSize);
        row();
        add(actor);
        staminaBar = new Bar(Color.CYAN);
        add(staminaBar)
                .growX();

        row();

        actor = new TextureActor(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        actor.setSize(iconSize, iconSize);
        add(actor);
        radiationBar = new Bar(Color.GREEN);
        add(radiationBar)
                .growX();

        distanceLabel = new Label("", userInterface.getLabelStyle());
        row();
        add(distanceLabel).colspan(2).left();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.updateValue(playerSystem.getHealth());
        staminaBar.updateValue(playerSystem.getStamina());
        radiationBar.updateValue(playerSystem.getRadiation());

        int dist = playerSystem.getDistance();
        if (dist > 5) {
            distanceLabel.setText(dist + " м.");
        }else
            distanceLabel.setText("");
    }

}
