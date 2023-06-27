package net.artux.pda.map.view.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.scenes.SceneManager;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.di.components.CoreComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;

import javax.inject.Inject;

@PerGameMap
public class MapsWidget extends Table {

    @Inject
    public MapsWidget(Skin skin, DataRepository dataRepository, SceneManager sceneManager, CoreComponent coreComponent, FontManager fontManager) {
        super(skin);
        left();
        defaults().left();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));

        StoryModel storyModel = dataRepository.getStoryModel();
        Label.LabelStyle labelStyle = new Label.LabelStyle(fontManager.getFont(48), Color.GRAY);

        for (GameMap map :
                storyModel.getMaps().values()) {
            Label label = new Label(map.getTitle(), labelStyle);
            add(label);
            row();
            label.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    dataRepository.setGameMap(map);
                    sceneManager.set(coreComponent.getPreloadState());
                }
            });
        }
    }


}
