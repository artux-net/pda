package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.quest.story.StoryDataModel;

public class WeaponComponent implements Component {

    private ArmorModel armor;
    private WeaponModel weaponModel1;
    private WeaponModel weaponModel2;
    int selected = 0;

    int bullets_2;
    int bullets_1;

    int stack;

    float timeout;

    StoryDataModel dataModel;
    boolean player;

    public WeaponComponent(StoryDataModel dataModel) {
        updateData(dataModel);
    }

    public void updateData(StoryDataModel dataModel) {
        this.dataModel = dataModel;
        this.armor = (ArmorModel) dataModel.getCurrentWearable(ItemType.ARMOR);
        this.weaponModel1 = (WeaponModel) dataModel.getCurrentWearable(ItemType.RIFLE);
        this.weaponModel2 = (WeaponModel) dataModel.getCurrentWearable(ItemType.PISTOL);

        WeaponModel selectedWeaponModel = getSelected();
        if (selectedWeaponModel != null)
            setResource(dataModel.getItemByBaseId(selectedWeaponModel.getBulletId()));

        player = true;
        reload();
    }

    public WeaponComponent(ArmorModel armor, WeaponModel weaponModel1, WeaponModel weaponModel2) {
        this.armor = armor;
        this.weaponModel1 = weaponModel1;
        this.weaponModel2 = weaponModel2;
        reload();
    }

    public ArmorModel getArmor() {
        return armor;
    }

    public void setArmor(ArmorModel armor) {
        this.armor = armor;
    }

    public ItemModel resource;

    public void setResource(ItemModel item) {
        if (item != null && getSelected().getBulletId() == item.getBaseId()) {
            resource = item;
        }
    }

    public int getMagazine() {
        int magazine;
        if (selected == 0)
            magazine = bullets_2;
        else
            magazine = bullets_1;
        return magazine;
    }

    public WeaponModel getSelected() {
        if (selected == 0 && weaponModel1 != null)
            return weaponModel1;
        else {
            selected = 1;
            return weaponModel2;
        }
    }

    public void setWeapon(WeaponModel weaponModel, int id) {
        if (id == 0)
            weaponModel1 = weaponModel;
        else weaponModel2 = weaponModel;
    }

    public void update(float dt) {
        if (timeout > 0)
            timeout -= dt;
        if (timeout < 0)
            reloading = false;

        if (resource != null && resource.getQuantity() < 1) {
            //swap
            WeaponModel another = getSelected() == weaponModel1 ? weaponModel2 : weaponModel1;
            if (another != null) {
                ItemModel bullet = dataModel.getItemByBaseId(getSelected().getBulletId());
                if (bullet != null && bullet.getQuantity() > 0) {
                    selected = selected == 0 ? 1 : 0;
                    setResource(bullet);
                }
            }
        }
    }

    public boolean reloading;

    public boolean shoot() {
        if (timeout <= 0 && ((resource != null && resource.getQuantity() > 0) || !player)) {
            WeaponModel weaponModel = getSelected();
            int magazine;
            if (selected == 0)
                magazine = bullets_2;
            else
                magazine = bullets_1;

            if (stack < 4 && magazine > 0) {
                if (selected == 0)
                    bullets_2--;
                else
                    bullets_1--;

                timeout += 1 / weaponModel.getSpeed();
                stack++;
                if (player)
                    resource.setQuantity(resource.getQuantity() - 1);
                ;
                return true;
            } else if (magazine == 0) {
                reloading = true;

                reload();
                timeout += 20 / weaponModel.getSpeed(); // перезарядка
                return false;
            } else {
                stack = 0;
                timeout += 10 / weaponModel.getSpeed();
                return false;
            }
        } else return false;
    }


    void reload() {
        WeaponModel weaponModel = getSelected();
        if (weaponModel != null && (!player || (resource != null && resource.getQuantity() > 0))) {
            int take = weaponModel.getBulletQuantity();
            if (player) {
                take = resource.getQuantity();
                if (weaponModel.getBulletQuantity() < take) {
                    take = weaponModel.getBulletQuantity();
                }
            }
            stack = 0;
            if (selected == 0)
                bullets_2 = take;
            else
                bullets_1 = take;
        }
    }
}