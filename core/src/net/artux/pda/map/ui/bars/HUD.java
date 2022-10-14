package net.artux.pda.map.ui.bars;


import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.systems.PlayerSystem;
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
        Image actor = new Image(assetManager.get("ui/bar/ic_health.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
        healthBar = new Bar(Color.RED);
        add(healthBar)
                .fillX()
                .expandX();

        actor = new Image(assetManager.get("ui/bar/ic_stamina.png", Texture.class));
        actor.setScaling(Scaling.fill);
        row();
        add(actor).size(iconSize);
        staminaBar = new Bar(Color.CYAN);
        add(staminaBar)
                .growX();

        row();

        actor = new Image(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
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
        HealthComponent healthComponent = playerSystem.getHealthComponent();

        healthBar.updateValue(healthComponent.value);
        staminaBar.updateValue(healthComponent.stamina);
        radiationBar.updateValue(healthComponent.radiation);

        int dist = playerSystem.getDistance();
        if (dist > 5) {
            distanceLabel.setText(dist + " м.");
        }else
            distanceLabel.setText("");
    }

}
