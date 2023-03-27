package net.artux.pda.map.view.view;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.systems.player.MissionsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.view.UserInterface;

import javax.inject.Inject;

@PerGameMap
public class DetailedHUD extends HUD {

    private final MissionsSystem missionsSystem;
    private final Label distanceLabel;
    private final Image directionImage;

    @Inject
    public DetailedHUD(AssetManager assetManager, PlayerSystem playerSystem, MissionsSystem missionsSystem, UserInterface userInterface) {
        super(assetManager, playerSystem);
        this.missionsSystem = missionsSystem;

        distanceLabel = new Label("", userInterface.getLabelStyle());
        row();

        add(distanceLabel)
                .growX()
                .left()
                .colspan(2);

        directionImage = new Image(assetManager.get("textures/ui/direction.png", Texture.class));
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
