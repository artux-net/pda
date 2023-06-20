package net.artux.pda.map.view.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class PDADialog extends Dialog {

    private final Label titleLabel;

    public PDADialog(String title, Skin skin) {
        super("", skin);

        WindowStyle style = skin.get(WindowStyle.class);
        titleLabel = newLabel(title, new Label.LabelStyle(style.titleFont, style.titleFontColor));

        row();
        pad(10f);
        getContentTable().add(titleLabel).row();
        getContentTable().defaults().space(10);

        setModal(true);
        setMovable(true);
        setResizable(false);

        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    hide();
                }
                return true;
            }
        });
    }

    @Override
    public Label getTitleLabel() {
        return titleLabel;
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        hide();
        cancel();
    }
}
