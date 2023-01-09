package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WeaponSound;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.Iterator;
import java.util.List;
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

    public WeaponComponent(StoryDataModel dataModel, AssetManager assetManager) {
        this.assetManager = assetManager;
        updateData(dataModel);
    }

    public void updateData(StoryDataModel dataModel) {
        this.dataModel = dataModel;
        setWeaponModel((WeaponModel) dataModel.getCurrentWearable(ItemType.RIFLE));
        player = true;
        reload();
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

    public void switchWeapons() {
        List<WeaponModel> weaponModelList = dataModel.getWeapons();
        Iterator<WeaponModel> iterator = weaponModelList.listIterator();
        WeaponModel old = weaponModel;
        while (iterator.hasNext()) {
            WeaponModel next = iterator.next();
            if (weaponModel == next && iterator.hasNext()) {
                setWeaponModel(iterator.next());
            }
        }
        if (old == weaponModel && weaponModelList.size() > 1)
            setWeaponModel(weaponModelList.get(0));
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

    public boolean reloading;

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
        reloading = true;
        WeaponModel weaponModel = getSelected();
        if (weaponModel != null) {
            int take = weaponModel.getBulletQuantity();
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