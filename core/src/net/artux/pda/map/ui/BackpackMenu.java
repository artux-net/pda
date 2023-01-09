package net.artux.pda.map.ui;

import static net.artux.pda.model.QuestUtil.isInteger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.ui.blocks.MediaItem;
import net.artux.pda.map.ui.blocks.SlotTextButton;
import net.artux.pda.map.ui.units.LazyImage;
import net.artux.pda.map.ui.view.ItemView;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.text.DecimalFormat;

import javax.inject.Inject;

@PerGameMap
public class BackpackMenu extends Table {

    private final SoundsSystem soundsSystem;
    private final PlayerSystem playerSystem;
    private final DataRepository dataRepository;
    private final AssetManager assetManager;
    private final FontManager fontManager;

    private final MediaItem mediaItem;
    private final Label additionalLabel;
    private final Table mainContent;
    private final Table additionalContent;

    @Inject
    public BackpackMenu(HUD hud, SlotTextButton textButton, AssetsFinder assetsFinder, PlayerSystem playerSystem, DataRepository dataRepository, Skin skin, SoundsSystem soundsSystem) {
        super();
        this.playerSystem = playerSystem;
        this.dataRepository = dataRepository;
        this.assetManager = assetsFinder.getManager();
        this.soundsSystem = soundsSystem;
        top();
        defaults()
                .pad(10)
                .space(20);

        fontManager = assetsFinder.getFontManager();
        Label.LabelStyle labelStyle = fontManager.getLabelStyle(38, Color.WHITE);
        Label.LabelStyle subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY);

        textButton.setText("Закрыть");
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
            }
        });
        add(textButton)
                .left();
        row();
        UserModel userModel = dataRepository.getUserModel();


        Image avatar;
        if (isInteger(userModel.getAvatar()) && !userModel.getAvatar().equals("0"))
            avatar = new LazyImage(assetManager, "avatars/a" + userModel.getAvatar() + ".png");
        else
            avatar = new LazyImage(assetManager, "avatars/a0.jpg");

        mediaItem = new MediaItem(avatar, "nickname", "content", labelStyle, subtitleStyle);

        add(mediaItem)
                .uniformY();

        add(hud)
                .fill()
                .growX();
        row();

        Label label = new Label("Инвентарь", labelStyle);
        add(label)
                .left()
                .growX();
        row();

        mainContent = new Table();
        mainContent
                .align(Align.left | Align.top)
                .left()
                .defaults()
                .pad(5)
                .space(15);

        mainContent.debugCell();

        ScrollPane scrollPane = new ScrollPane(mainContent, skin);
        scrollPane.setScrollingDisabled(false, true);

        add(scrollPane)
                .fillY()
                .growX()
                .uniformY()
                .colspan(2);
        row();
        additionalLabel = new Label("Быстрый доступ", labelStyle);
        add(additionalLabel)
                .left()
                .growX();
        row();


        additionalContent = new Table();
        additionalContent
                .align(Align.left | Align.top)
                .left()
                .defaults()
                .pad(5)
                .space(15);

        additionalContent.debugCell();

        scrollPane = new ScrollPane(additionalContent, skin);
        scrollPane.setScrollingDisabled(false, true);
        add(scrollPane)
                .fillY()
                .growX()
                .uniformY()
                .colspan(2);

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    private final DecimalFormat formater = new DecimalFormat("##.##");

    public void update() {
        mainContent.clear();
        additionalContent.clear();

        StoryDataModel dataModel = dataRepository.getStoryDataModel();

        Label.LabelStyle titleStyle = fontManager.getLabelStyle(28, Color.WHITE);
        Label.LabelStyle subtitleStyle = fontManager.getLabelStyle(22, Color.GRAY);

        float weightSum = 0;

        for (ItemModel itemModel : dataModel.getAllItems()) {
            if (itemModel.getQuantity() > 0) {
                weightSum += itemModel.getQuantity() * itemModel.getWeight();

                ItemView itemView = new ItemView(itemModel, titleStyle, subtitleStyle, assetManager);
                if (itemModel instanceof MedicineModel) {
                    itemView.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            MedicineModel model = (MedicineModel) itemModel;
                            if (model.getQuantity() > 0) {
                                model.setQuantity(model.getQuantity() - 1);
                                playerSystem.getHealthComponent().treat((MedicineModel) itemModel);
                                soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"));
                            }
                            super.clicked(event, x, y);
                            update();
                        }
                    });
                    additionalContent.add(itemView)
                            .uniform()
                            .fill();
                } else
                    mainContent.add(itemView)
                            .uniform()
                            .fill();
            }
        }

        UserModel userModel = dataRepository.getUserModel();
        String nickname = userModel.getName() + " " + userModel.getNickname();
        String content = "Денег: " + userModel.getMoney() + " RU" + "\nВес рюкзака: " + formater.format(weightSum) + " кг.";
        mediaItem.setTitle(nickname);
        mediaItem.setSubtitle(content);
    }

}
