package net.artux.pda.map.ui.bars;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.systems.player.MissionsSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.ui.UserInterface;

import javax.inject.Inject;

public class HUD extends Button {

    private final PlayerSystem playerSystem;
    private Bar healthBar;
    private Bar staminaBar;
    private Bar radiationBar;
    private MissionsSystem missionsSystem;
    private Label distanceLabel;
    private Image directionImage;

    @Inject
    public HUD(AssetManager assetManager, PlayerSystem playerSystem, MissionsSystem missionsSystem, UserInterface userInterface) {
        super();
        this.playerSystem = playerSystem;
        this.missionsSystem = missionsSystem;

        top();
        left();

        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(assetManager.get("ui/bar/hudBackground.png", Texture.class));
        ButtonStyle buttonStyle = new ButtonStyle();
        buttonStyle.up = textureRegionDrawable;
        setStyle(buttonStyle);

        setSize(textureRegionDrawable.getRegion().getRegionWidth(), textureRegionDrawable.getRegion().getRegionHeight());
        defaults().pad(5);
        float iconSize = 18f;
        Image actor = new Image(assetManager.get("ui/bar/ic_health.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
        healthBar = new Bar(Color.RED);
        add(healthBar)
                .growX()
                .colspan(2);

        actor = new Image(assetManager.get("ui/bar/ic_stamina.png", Texture.class));
        actor.setScaling(Scaling.fill);
        row();
        add(actor).size(iconSize);
        staminaBar = new Bar(Color.CYAN);
        add(staminaBar)
                .growX()
                .colspan(2);

        row();

        actor = new Image(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        actor.setScaling(Scaling.fill);
        add(actor).size(iconSize);
        radiationBar = new Bar(Color.GREEN);
        add(radiationBar)
                .growX()
                .colspan(2);

        distanceLabel = new Label("", userInterface.getLabelStyle());
        row();

        add(distanceLabel)
                .growX()
                .left()
                .colspan(2);

        directionImage = new Image(assetManager.get("ui/direction.png", Texture.class));
        directionImage.setScaling(Scaling.fit);
        directionImage.setOrigin(Align.center);

        add(directionImage)
                .colspan(1)
                .right()
                .padRight(20);
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

        healthBar.updateValue(healthComponent.value);
        staminaBar.updateValue(healthComponent.stamina);
        radiationBar.updateValue(healthComponent.radiation);

        int dist = missionsSystem.getTargetDistance();
        if (dist > 5) {
            distanceLabel.setText(dist + " м.");
            directionImage.setVisible(true);
            directionImage.setRotation((float) missionsSystem.getTargetAngle());
        } else {
            distanceLabel.setText("");
            directionImage.setVisible(false);
        }
    }

}
