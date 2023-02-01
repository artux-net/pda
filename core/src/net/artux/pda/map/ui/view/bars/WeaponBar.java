package net.artux.pda.map.ui.view.bars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.ui.UserInterface;

public class WeaponBar extends VerticalGroup implements Disposable {

    private final Label selected;
    private final Label resource;
    private final TextureAtlas skinAtlas;
    private final NinePatchDrawable barBackground;

    private int padding = 10;

    UserInterface userInterface;

    public WeaponBar(UserInterface userInterface) {
        super();

        this.userInterface = userInterface;

        skinAtlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        barBackground = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-scroll"), 4, 5, 4, 5));


        align(Align.left | Align.center);

        selected = new Label("", userInterface.getLabelStyle());
        resource = new Label("", userInterface.getLabelStyle());
        addActor(selected);
        addActor(resource);
        fill();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        selected.setText(PlayerData.selectedWeapon);
        if (PlayerData.bullet != null)
            resource.setText(PlayerData.bullet + ": " + PlayerData.magazine+"/" + PlayerData.resource);
        else
            resource.setText("Патроны отсутствуют");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float w = getWidth() * getScaleX();
        float h = getHeight() * getScaleY();
        if (selected.getText().length>0) {
            barBackground.draw(batch, getX() - padding, getY() - padding, w + padding * 2, h + padding * 2);
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public void dispose() {
        skinAtlas.dispose();
    }
}
