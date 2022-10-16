package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.Iterator;
import java.util.List;

public class WeaponComponent implements Component {

    private ArmorModel armor;
    private WeaponModel weaponModel;
    private ItemModel bulletModel;

    int bullets;

    int stack;

    float timeout;

    StoryDataModel dataModel;
    boolean player;

    boolean shootLastFrame;

    public boolean isShootLastFrame() {
        return shootLastFrame;
    }

    public WeaponComponent(StoryDataModel dataModel) {
        updateData(dataModel);
    }

    public void updateData(StoryDataModel dataModel) {
        this.dataModel = dataModel;
        this.armor = (ArmorModel) dataModel.getCurrentWearable(ItemType.ARMOR);
        setWeaponModel((WeaponModel) dataModel.getCurrentWearable(ItemType.RIFLE));

        player = true;
        reload();
    }

    public WeaponComponent(ArmorModel armor, WeaponModel weaponModel1) {
        this.armor = armor;
        this.weaponModel = weaponModel1;
        reload();
    }

    public void setWeaponModel(WeaponModel weaponModel) {
        this.weaponModel = weaponModel;
        if (weaponModel != null) {
            setBulletModel(dataModel.getItemByBaseId(weaponModel.getBulletId()));
            reload();
        }
    }

    public ItemModel getBulletModel() {
        return bulletModel;
    }

    public ArmorModel getArmor() {
        return armor;
    }

    public void setArmor(ArmorModel armor) {
        this.armor = armor;
    }

    public void setBulletModel(ItemModel item) {
        if (item != null && getSelected().getBulletId() == item.getBaseId()) {
            bulletModel = item;
        }
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
                stack++;
                if (player)
                    bulletModel.setQuantity(bulletModel.getQuantity() - 1);
                shootLastFrame = true;
            } else if (magazine == 0) {
                reloading = true;

                reload();
                timeout += 20 / weaponModel.getSpeed(); // перезарядка
                shootLastFrame = false;
            } else {
                stack = 0;
                timeout += 10 / weaponModel.getSpeed();
                shootLastFrame = false;
            }
        } else shootLastFrame = false;
        return shootLastFrame;
    }


    void reload() {
        WeaponModel weaponModel = getSelected();
        if (weaponModel != null && (!player || (bulletModel != null && bulletModel.getQuantity() > 0))) {
            int take = weaponModel.getBulletQuantity();
            if (player) {
                take = bulletModel.getQuantity();
                if (weaponModel.getBulletQuantity() < take) {
                    take = weaponModel.getBulletQuantity();
                }
            }
            stack = 0;
            bullets = take;
        }
    }
}