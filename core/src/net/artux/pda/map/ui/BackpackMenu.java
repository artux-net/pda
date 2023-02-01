package net.artux.pda.map.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import net.artux.pda.map.ui.blocks.SlotTextButton;
import net.artux.pda.map.ui.view.DetailItemView;
import net.artux.pda.map.ui.view.HUD;
import net.artux.pda.map.ui.view.ItemsTableView;
import net.artux.pda.map.ui.view.OnItemClickListener;
import net.artux.pda.map.ui.view.bars.Utils;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class BackpackMenu extends Table {

    private final DataRepository dataRepository;
    private final AssetManager assetManager;
    private final FontManager fontManager;

    private final Label infoLabel;
    private final LocaleBundle localeBundle;
    private final ItemsTableView mainItemsView;

    private final DetailItemView armorView;
    private final DetailItemView rifleView;
    private final DetailItemView pistolView;

    @Inject
    public BackpackMenu(UserInterface userInterface, PlatformInterface platformInterface, HUD hud, SlotTextButton textButton,
                        SlotTextButton adButton, AssetsFinder assetsFinder, PlayerSystem playerSystem,
                        LocaleBundle localeBundle, DataRepository dataRepository, Skin skin, SoundsSystem soundsSystem) {
        super();
        this.localeBundle = localeBundle;
        this.dataRepository = dataRepository;
        this.assetManager = assetsFinder.getManager();
        top();
        setFillParent(true);
        defaults()
                .pad(10)
                .space(20);

        defaults().fill();


        fontManager = assetsFinder.getFontManager();
        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);
        Label.LabelStyle subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY);

        Table leftTable = new Table();
        leftTable.top().left();

        textButton.setText(localeBundle.get("main.close"));
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
            }
        });

        adButton.setText(localeBundle.get("main.ad.rewarded"));
        adButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                platformInterface.rewardedAd();
            }
        });

        infoLabel = new Label("", subtitleStyle);

        VerticalGroup verticalGroup = new VerticalGroup();

        Table buttonsTable = new Table();
        buttonsTable.center();
        buttonsTable.defaults().center().space(20);
        buttonsTable.add(textButton).uniformX();
        buttonsTable.add(adButton).uniformX();
        verticalGroup.addActor(buttonsTable);

        buttonsTable = new Table();
        buttonsTable.center();
        buttonsTable.defaults().space(20).center();
        buttonsTable.add(hud);
        buttonsTable.add(infoLabel);
        buttonsTable.row();


        verticalGroup.addActor(buttonsTable);

        leftTable.add(verticalGroup)
                .left().top().row();

        armorView = new DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager);
        leftTable.add(armorView)
                .fill();

        Table horizontalGroup = new Table();
        horizontalGroup.center();
        rifleView = new DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager);
        pistolView = new DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager);
        pistolView.disableDesc();
        rifleView.disableDesc();
        armorView.disableDesc();
        horizontalGroup.add(rifleView).fill().uniform();
        horizontalGroup.add(pistolView).fill().uniform();

        leftTable.row();
        leftTable.add(horizontalGroup).growX().center();

        add(leftTable)
                .colspan(1)
                .left()
                .uniformX().fill();

        mainItemsView = new ItemsTableView(localeBundle.get("main.inventory"), titleLabelStyle, subtitleStyle, assetsFinder, skin);
        add(mainItemsView)
                .left()
                .colspan(2)
                .growX();

        OnItemClickListener onItemClickListener = itemModel -> {
            if (itemModel instanceof MedicineModel) {
                MedicineModel model = (MedicineModel) itemModel;
                if (model.getQuantity() > 0) {
                    model.setQuantity(model.getQuantity() - 1);
                    playerSystem.getHealthComponent().treat((MedicineModel) itemModel);
                    soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"));
                }
                update();
            } else if (itemModel instanceof WearableModel) {
                dataRepository.getStoryDataModel().setCurrentWearable((WearableModel) itemModel);
                dataRepository.updateDataModel();
                update();
                soundsSystem.playSound(assetManager.get("audio/sounds/person/equip.ogg"));
            }
        };
        mainItemsView.setOnClickListener(onItemClickListener);

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
        setTouchable(Touchable.enabled);

        dataRepository.addPropertyChangeListener(propertyChangeEvent -> {
            if (propertyChangeEvent.getPropertyName().equals("storyData")) {
                update();
            }
        });
    }

    public void update() {
        StoryDataModel dataModel = dataRepository.getStoryDataModel();
        List<ItemModel> models = dataModel.getAllItems();

        mainItemsView.update(models);

        infoLabel.setText(localeBundle.get("user.info", dataModel.getMoney(), dataModel.getXp()));
        updateWearableInfo();
    }

    public void updateWearableInfo() {
        ArmorModel armorModel = (ArmorModel) dataRepository.getStoryDataModel().getEquippedWearable(ItemType.ARMOR);
        armorView.setWearableModel(armorModel);
        WeaponModel weaponModel = (WeaponModel) dataRepository.getStoryDataModel().getEquippedWearable(ItemType.RIFLE);
        rifleView.setWearableModel(weaponModel);
        weaponModel = (WeaponModel) dataRepository.getStoryDataModel().getEquippedWearable(ItemType.PISTOL);
        pistolView.setWearableModel(weaponModel);
    }

}
