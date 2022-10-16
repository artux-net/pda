package net.artux.pda.map.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.quest.story.StoryDataModel;

public class BackpackMenu extends Table implements Disposable {

    private Skin skin;
    private UserInterface userInterface;
    private Label label;
    private Table content;

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

    public BackpackMenu(final UserInterface userInterface, Skin skin) {
        super();
        this.userInterface = userInterface;
        this.skin = skin;
        setSkin(skin);

        Table hudTable = userInterface.getAssistantBlock();
        hudTable.row();

        top();
        left();

        label = new Label("Рюкзак", userInterface.getLabelStyle());
        top();
        add(label);
        row();
        content = new Table();
        content.defaults().align(Align.left);
        content.left();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX();

        setBackground(Utils.getColoredDrawable(1, 1, Color.BLACK));
    }


    @Override
    public void dispose() {
    }
}
