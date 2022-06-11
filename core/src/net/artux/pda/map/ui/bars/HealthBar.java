package net.artux.pda.map.ui.bars;

import static net.artux.pdalib.Checker.isInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.ui.UserInterface;

public class HealthBar extends VerticalGroup implements Disposable {

    private final TextureAtlas skinAtlas;
    private final NinePatchDrawable barBackground;
    private final NinePatchDrawable healthBar;
    private final NinePatchDrawable healthBarBackground;

    private int padding = 10;

    UserInterface userInterface;
    Texture image;
    public HealthBar(UserInterface userInterface, AssetManager assetManager) {
        super();
        this.userInterface = userInterface;

        skinAtlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        healthBar = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4));
        healthBarBackground = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4));
        barBackground = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-scroll"), 4, 5, 4, 5));
        padTop(20);
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.align(Align.left | Align.center);
        horizontalGroup.space(20);
        horizontalGroup.setFillParent(true);

        if(isInteger(userInterface.getMember().getAvatar()))
            image = assetManager
                    .get("avatars/a"+(Integer.parseInt(userInterface.getMember().getAvatar())+1)+".png", Texture.class);
        else
            image = assetManager
                    .get("avatars/a0.jpg", Texture.class);

        horizontalGroup.addActor(new Image(image));

        VerticalGroup labelsTable = new VerticalGroup();
        labelsTable.align(Align.left|Align.top);
        labelsTable.fill();
        Label.LabelStyle style = userInterface.getLabelStyle();

        labelsTable.addActor(new Label(userInterface.getMember().getName() + " " + userInterface.getMember().getNickname()
                + " [PDA #"+ userInterface.getMember().getPdaId()+"]", style));
        labelsTable.addActor(new Label("Денег: " + userInterface.getMember().getMoney(), style));

        horizontalGroup.addActor(labelsTable);

        horizontalGroup.fill();
        addActor(horizontalGroup);
        fill();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float progress = 1f;

        float w = getWidth() * getScaleX();
        float h = getHeight() * getScaleY();

        barBackground.draw(batch, getX() - padding, getY() - padding, w + padding*2, h + padding*2);
        healthBarBackground.draw(batch, getX(), getY(), w, 0.1f * h);
        healthBar.draw(batch, getX(), getY(), progress * w , 0.1f * h);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        skinAtlas.dispose();
    }
}
