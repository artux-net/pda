package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

import net.artux.pda.map.DataRepository;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WeaponSound;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.Locale;

public class WeaponComponent implements Component {

    private static final String prefix = "audio/sounds/weapons/";

    private static final String rifleDefaultShotSound = prefix + "rifle/ak74_shot.ogg";
    private static final String rifleDefaultReloadSound = prefix + "rifle/ak74_reload.ogg";
    private static final String pistolDefaultShotSound = prefix + "pistol/pm_shot.ogg";
    private static final String pistolDefaultReloadSound = prefix + "pistol/pm_reload.ogg";

    private WeaponModel weaponModel;
    private ItemModel bulletModel;

    private StoryDataModel dataModel;

    private int bullets;
    private int stack;
    float timeout;

    boolean player;
    boolean shootLastFrame;

    private Sound shot;
    private Sound reload;


    private final AssetManager assetManager;

    public WeaponComponent(DataRepository dataRepository, AssetManager assetManager) {
        this.assetManager = assetManager;
        dataRepository.addPropertyChangeListener(propertyChangeEvent -> {
            if (propertyChangeEvent.getPropertyName().equals("storyData")) {
                updateData((StoryDataModel) propertyChangeEvent.getNewValue());
            }
        });
        updateData(dataRepository.getStoryDataModel());
    }

    public void updateData(StoryDataModel dataModel) {
        this.dataModel = dataModel;
        player = true;
        switchWeapons();
    }

    public WeaponComponent(WeaponModel weaponModel, AssetManager assetManager) {
        this.assetManager = assetManager;
        player = false;
        setWeaponModel(weaponModel);
        reload();
    }

    public void setWeaponModel(WeaponModel weaponModel) {
        this.weaponModel = weaponModel;
        if (weaponModel != null) {
            if (player)
                setBulletModel(dataModel.getItemByBaseId(weaponModel.getBulletId()));

            String reloadSoundName;
            String shotSoundName;

            if (weaponModel.getType() == ItemType.RIFLE) {
                reloadSoundName = rifleDefaultReloadSound;
                shotSoundName = rifleDefaultShotSound;
            } else {
                shotSoundName = pistolDefaultShotSound;
                reloadSoundName = pistolDefaultReloadSound;
            }

            WeaponSound sounds = weaponModel.getSounds();
            if (sounds != null) {
                String type = weaponModel.getType().name().toLowerCase(Locale.ROOT);

                reloadSoundName = prefix + type + "/" + sounds.getReload();
                shotSoundName = prefix + type + "/" + sounds.getShot();
            }

            shot = assetManager.get(shotSoundName);
            reload = assetManager.get(reloadSoundName);

            reload();
            reloading = false;
        }
    }

    public ItemModel getBulletModel() {
        return bulletModel;
    }

    public void setBulletModel(ItemModel item) {
        if (item != null && getSelected().getBulletId() == item.getBaseId()) {
            bulletModel = item;
        }
    }

    public Sound getReloadSound() {
        return reload;
    }

    public Sound getShotSound() {
        return shot;
    }

    public int getMagazine() {
        return bullets;
    }

    public WeaponModel getSelected() {
        return weaponModel;
    }

    ItemType type = ItemType.RIFLE;

    public void switchWeapons() {
        if (type == ItemType.RIFLE)
            type = ItemType.PISTOL;
        else
            type = ItemType.RIFLE;
        setWeaponModel((WeaponModel) dataModel.getEquippedWearable(type));
    }

    public void update(float dt) {
        if (timeout > 0)
            timeout -= dt;
        if (timeout < 0)
            reloading = false;

       /* if (resource != null && resource.getQuantity() < 1) {
            switchWeapons();
        }*/
    }

    public boolean reloading = false;

    public boolean shoot() {
        if (timeout <= 0 && ((bulletModel != null && bulletModel.getQuantity() > 0) || !player)) {
            WeaponModel weaponModel = getSelected();
            int magazine = bullets;

            if (stack < 4 && magazine > 0) {
                bullets--;

                timeout += 1 / weaponModel.getSpeed();

                if (player)
                    bulletModel.setQuantity(bulletModel.getQuantity() - 1);
                stack++;
                shootLastFrame = true;
            } else if (magazine == 0) {
                reload();
                timeout += 30 / weaponModel.getSpeed(); // перезарядка
                shootLastFrame = false;
            } else {
                stack = 0;
                timeout += 20 / weaponModel.getSpeed();
                shootLastFrame = false;
            }
        } else shootLastFrame = false;

        return shootLastFrame;
    }

    public void reload() {
        WeaponModel weaponModel = getSelected();
        if (weaponModel != null) {
            int take = weaponModel.getBulletQuantity();
            reloading = true;
            if (player) {
                if (bulletModel != null) {
                    take = bulletModel.getQuantity();
                    if (weaponModel.getBulletQuantity() < take) {
                        take = weaponModel.getBulletQuantity();
                    }
                } else
                    take = 0;
            }
            stack = 0;
            bullets = take;
        }
    }
}