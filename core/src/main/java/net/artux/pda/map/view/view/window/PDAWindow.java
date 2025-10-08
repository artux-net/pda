package net.artux.pda.map.view.view.window;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.Utils;

public abstract class PDAWindow extends Window {

    public PDAWindow(Skin skin) {
        super("", skin);
        setFillParent(true);
        top();
        left();

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundAlphaColor));
        addListener(new ClickListener());
    }

}
