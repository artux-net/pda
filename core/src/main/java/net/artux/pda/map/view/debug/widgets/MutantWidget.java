package net.artux.pda.map.view.debug.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.content.entities.MutantType;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.ai.TargetMovingComponent;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.Utils;
import net.artux.pda.map.view.root.FontManager;

import javax.inject.Inject;

@PerGameMap
public class MutantWidget extends Table {

    @Inject
    public MutantWidget(Skin skin, EntityBuilder entityBuilder,
                        EntityProcessorSystem entityProcessorSystem,
                        LocaleBundle localeBundle,
                        PlayerSystem playerSystem,
                        FontManager fontManager) {
        super(skin);
        left();
        defaults().left();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));

        Label.LabelStyle labelStyle = new Label.LabelStyle(fontManager.getFont(48), Color.GRAY);

        for (MutantType mutantType : MutantType.values()) {
            addLabel(localeBundle.get(mutantType.getTitleId()), labelStyle, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    entityProcessorSystem.addEntity(entityBuilder.randomMutant(mutantType, new TargetMovingComponent.Targeting() {
                        @Override
                        public Vector2 getTarget() {
                            return playerSystem.getPosition().add(0, 50);
                        }
                    }));
                }
            });
        }
    }

    private void addLabel(String title, Label.LabelStyle labelStyle, ClickListener clickListener) {
        Label label = new Label(title, labelStyle);
        add(label);
        row();
        label.addListener(clickListener);
    }


}
