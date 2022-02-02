package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

import org.graalvm.compiler.lir.LIRInstruction;

import javax.swing.UIDefaults;

public class HealthBar extends Group implements Disposable {

    private final TextureAtlas skinAtlas;
    private final NinePatchDrawable barBackground;
    private final NinePatchDrawable healthBar;
    private final NinePatchDrawable healthBarBackground;
    private final BitmapFont font;

    private int padding = 10;

    UserInterface userInterface;

    public HealthBar(UserInterface userInterface) {
        this.userInterface = userInterface;

        skinAtlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        healthBar = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4));
        healthBarBackground = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4));
        barBackground = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("default-scroll"), 4, 5, 4, 5));
        font = Fonts.generateFont(Fonts.Language.RUSSIAN, 32);

        Label label = new Label(userInterface.getMember().getName() + " " + userInterface.getMember().getNickname()
                + " [PDA #"+ userInterface.getMember().getPdaId()+"]", userInterface.getLabelStyle());
        addActor(label);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float progress = Logger.LogData.health / 100;

        float w = getWidth() * getScaleX();
        float h = getHeight() * getScaleY();

        barBackground.draw(batch, getX() - padding, getY() - padding, w + padding*2, h + padding*2);

        healthBarBackground.draw(batch, getX(), getY(), w, 0.1f * h);
        healthBar.draw(batch, getX(), getY(), progress * w , 0.1f * h);
    }

    @Override
    public void dispose() {
        font.dispose();
        skinAtlas.dispose();
    }
}
