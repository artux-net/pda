package net.artux.pda.map.view.view;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.view.view.bars.Bar;

import javax.inject.Inject;

public class HUD extends Button {

    private final PlayerSystem playerSystem;
    private final Bar healthBar;
    private final Bar staminaBar;
    private final Bar radiationBar;

    @Inject
    public HUD(AssetManager assetManager, PlayerSystem playerSystem) {
        super();
        this.playerSystem = playerSystem;

        top();
        left();

        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(assetManager.get("textures/ui/bar/hudBackground.png", Texture.class));
        ButtonStyle buttonStyle = new ButtonStyle();
        buttonStyle.up = textureRegionDrawable;
        setStyle(buttonStyle);

        setSize(textureRegionDrawable.getRegion().getRegionWidth(), textureRegionDrawable.getRegion().getRegionHeight());
        defaults().pad(5);
        float iconSize = 18f;
        Image actor = new Image(assetManager.get("textures/ui/bar/ic_health.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
        healthBar = new Bar(Color.RED);
        add(healthBar)
                .growX()
                .colspan(2);

        actor = new Image(assetManager.get("textures/ui/bar/ic_stamina.png", Texture.class));
        actor.setScaling(Scaling.fill);
        row();
        add(actor).size(iconSize);
        staminaBar = new Bar(Color.CYAN);
        add(staminaBar)
                .growX()
                .colspan(2);

        row();

        actor = new Image(assetManager.get("textures/ui/bar/ic_radiation.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
        radiationBar = new Bar(Color.GREEN);
        add(radiationBar)
                .growX()
                .colspan(2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        HealthComponent healthComponent = playerSystem.getHealthComponent();
        if (healthComponent.isDead()) {
            healthBar.updateValue(0);
            staminaBar.updateValue(0);
            return;
        }

        healthBar.updateValue(healthComponent.getHealth());
        staminaBar.updateValue(healthComponent.getStamina());
        radiationBar.updateValue(healthComponent.getRadiation());
    }

}
