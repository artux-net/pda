package net.artux.pda.map.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;

import net.artux.pda.map.utils.model.Triple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Logger extends VerticalGroup {

    private final Label.LabelStyle labelStyle;
    private final List<Triple<Method, Object, String>> dataCollection;

    public static boolean visible = true;

    public Logger(Skin skin) {
        super();

        dataCollection = new ArrayList<>();
        BitmapFont font = skin.getFont("font");
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        columnAlign(Align.left);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (Actor l : getChildren()) {
            for (Triple<Method, Object, String> tr : dataCollection) {
                if (tr.getThird().equals(l.getName()))
                    try {
                        String result = tr.getFirst().invoke(tr.getSecond()).toString();

                        StringBuilder stringBuilder = ((Label) l).getText();
                        int index = stringBuilder.indexOf(":");
                        if (index > 0)
                            stringBuilder
                                    .delete(++index, stringBuilder.length)
                                    .append(result);
                        else
                            stringBuilder
                                    .append(tr.getThird())
                                    .append(":")
                                    .append(result);
                        ((Label) l).invalidate();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (visible)
            super.draw(batch, parentAlpha);
    }

    public void put(String title, Object o, String nameOfMethod) {
        Method method = null;
        try {
            if (nameOfMethod == null || nameOfMethod.equals(""))
                throw new NoSuchMethodException();
            method = o.getClass().getDeclaredMethod(nameOfMethod);

        } catch (NoSuchMethodException e) {
            try {
                method = o.getClass().getDeclaredMethod("toString");
            } catch (NoSuchMethodException ignored) {
                try {
                    method = o.getClass().getSuperclass().getDeclaredMethod(nameOfMethod);
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                }
            }
        } finally {
            if (method != null) {
                dataCollection.add(new Triple<>(method, o, title));
                Label label = new Label("", labelStyle);
                //label.setWrap(true);
                label.setName(title);
                addActor(label);
            }
        }
    }

}
