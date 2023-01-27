package net.artux.pda.map.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.ui.blocks.SlotTextButton;
import net.artux.pda.map.ui.view.DetailItemView;
import net.artux.pda.map.ui.view.ItemsHorizontalView;
import net.artux.pda.map.ui.view.OnItemClickListener;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@PerGameMap
public class BackpackMenu extends Table {

    private final SoundsSystem soundsSystem;
    private final PlayerSystem playerSystem;
    private final DataRepository dataRepository;
    private final AssetManager assetManager;
    private final FontManager fontManager;

    private final Label infoLabel;
    private final LocaleBundle localeBundle;
    private final ItemsHorizontalView mainItemsView;
    private final ItemsHorizontalView fastItemsView;

    @Inject
    public BackpackMenu(HUD hud, SlotTextButton textButton, AssetsFinder assetsFinder, PlayerSystem playerSystem,
                        LocaleBundle localeBundle, DataRepository dataRepository, Skin skin, SoundsSystem soundsSystem) {
        super();
        this.localeBundle = localeBundle;
        this.playerSystem = playerSystem;
        this.dataRepository = dataRepository;
        this.assetManager = assetsFinder.getManager();
        this.soundsSystem = soundsSystem;
        top();
        defaults()
                .pad(10)
                .space(20);

        fontManager = assetsFinder.getFontManager();
        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);
        Label.LabelStyle subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY);

        textButton.setText(localeBundle.get("main.close"));
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
            }
        });
        infoLabel = new Label("", subtitleStyle);

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.addActor(textButton);
        verticalGroup.addActor(hud);
        verticalGroup.addActor(infoLabel);

        add(verticalGroup)
                .left().top();

        ArmorModel armorModel = (ArmorModel) dataRepository.getStoryDataModel().getCurrentWearable(ItemType.ARMOR);

        if (armorModel != null)
            add(new DetailItemView(armorModel, titleLabelStyle, subtitleStyle, localeBundle, assetManager))
                    .fill()
                    .growX();

        verticalGroup = new VerticalGroup();
        WeaponModel weaponModel = (WeaponModel) dataRepository.getStoryDataModel().getCurrentWearable(ItemType.RIFLE);
        if (weaponModel != null)
            verticalGroup.addActor(new DetailItemView(weaponModel, titleLabelStyle, subtitleStyle, localeBundle, assetManager));

        weaponModel = (WeaponModel) dataRepository.getStoryDataModel().getCurrentWearable(ItemType.PISTOL);
        if (weaponModel != null)
            verticalGroup.addActor(new DetailItemView(weaponModel, titleLabelStyle, subtitleStyle, localeBundle, assetManager));

        add(verticalGroup)
                .growX();

        row();

        mainItemsView = new ItemsHorizontalView(localeBundle.get("main.inventory"), titleLabelStyle, subtitleStyle, assetsFinder, skin);
        add(mainItemsView)
                .colspan(3)
                .left()
                .growX();
        row();

        fastItemsView = new ItemsHorizontalView(localeBundle.get("main.fastaccess"), titleLabelStyle, subtitleStyle, assetsFinder, skin);
        add(fastItemsView)
                .colspan(3)
                .left()
                .growX();
        row();

        OnItemClickListener onItemClickListener = itemModel -> {
            if (itemModel instanceof MedicineModel) {
                MedicineModel model = (MedicineModel) itemModel;
                if (model.getQuantity() > 0) {
                    model.setQuantity(model.getQuantity() - 1);
                    playerSystem.getHealthComponent().treat((MedicineModel) itemModel);
                    soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"));
                }
                update();
            }else if (itemModel instanceof WearableModel){

            }
        };
        fastItemsView.setOnClickListener(onItemClickListener);
        mainItemsView.setOnClickListener(onItemClickListener);

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public void update() {
        StoryDataModel dataModel = dataRepository.getStoryDataModel();
        List<ItemModel> models = dataModel.getAllItems();
        List<ItemModel> medicines = models.stream()
                .filter(itemModel -> itemModel instanceof MedicineModel)
                .collect(Collectors.toList());

        mainItemsView.update(models);
        fastItemsView.update(medicines);

        UserModel userModel = dataRepository.getUserModel();
        infoLabel.setText(localeBundle.get("user.info", userModel.getMoney(), userModel.getXp()));
    }

}
