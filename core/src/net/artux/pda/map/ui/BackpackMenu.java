package net.artux.pda.map.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import javax.inject.Inject;

@PerGameMap
public class BackpackMenu extends Table {

    private UserInterface userInterface;
    private Label label;
    private Table content;

    @Inject
    public BackpackMenu(final UserInterface userInterface, DataRepository dataRepository, Skin skin) {
        super();
        this.userInterface = userInterface;

        top();

        label = new Label("Рюкзак", userInterface.getLabelStyle());
        add(label);
        row();
        content = new Table();
        content.defaults().align(Align.left);
        content.left();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX();

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public void update(StoryDataModel dataModel, PlayerSystem.MedicineListener medicineListener) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = userInterface.getLabelStyle().font;
        textButtonStyle.fontColor = userInterface.getLabelStyle().fontColor;
        content.clear();
        for (ItemModel itemModel : dataModel.getAllItems()) {
            if (itemModel.getQuantity() > 0) {
                content.row();
                TextButton textButton = new TextButton(itemModel.getTitle(), textButtonStyle);
                if (itemModel instanceof MedicineModel)
                    textButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            MedicineModel model = (MedicineModel) itemModel;
                            if (model.getQuantity() > 0) {
                                model.setQuantity(model.getQuantity() - 1);
                                medicineListener.treat((MedicineModel) itemModel);
                            }
                            super.clicked(event, x, y);
                        }
                    });
                content.add(textButton);
            }
        }
    }

}
