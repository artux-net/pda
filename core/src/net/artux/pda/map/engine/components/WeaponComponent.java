package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.map.models.items.Armor;
import net.artux.pda.map.models.items.Item;
import net.artux.pda.map.models.items.ItemType;
import net.artux.pda.map.models.items.Weapon;
import net.artux.pda.map.models.user.GdxData;

public class WeaponComponent implements Component {

    private Armor armor;
    private Weapon weapon1;
    private Weapon weapon2;
    int selected = 0;

    int bullets_2;
    int bullets_1;

    int stack;

    float timeout;

    GdxData dataModel;
    boolean player;

    public WeaponComponent(GdxData dataModel) {
        this.armor = (Armor) dataModel.getCurrentWearable(ItemType.ARMOR);
        this.weapon1 = (Weapon) dataModel.getCurrentWearable(ItemType.RIFLE);
        this.weapon2 = (Weapon) dataModel.getCurrentWearable(ItemType.PISTOL);

        Weapon selectedWeapon = getSelected();
        if (selectedWeapon != null)
            setResource(dataModel.getItemByBaseId(selectedWeapon.getBulletId()));

        player = true;
        reload();
    }

    public WeaponComponent(Armor armor, Weapon weapon1, Weapon weapon2) {
        this.armor = armor;
        this.weapon1 = weapon1;
        this.weapon2 = weapon2;
        reload();
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Item resource;

    public void setResource(Item item) {
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

    public Weapon getSelected() {
        if (selected == 0 && weapon1 != null)
            return weapon1;
        else {
            selected = 1;
            return weapon2;
        }
    }

    public void setWeapon(Weapon weapon, int id) {
        if (id == 0)
            weapon1 = weapon;
        else weapon2 = weapon;
    }

    public void update(float dt) {
        if (timeout > 0)
            timeout -= dt;
        if (timeout < 0)
            reloading = false;

        if (resource != null && resource.quantity < 1) {
            //swap
            Weapon another = getSelected() == weapon1 ? weapon2 : weapon1;
            if (another != null) {
                Item bullet = dataModel.getItemByBaseId(getSelected().getBulletId());
                if (bullet != null && bullet.quantity > 0) {
                    selected = selected == 0 ? 1 : 0;
                    setResource(bullet);
                }
            }
        }
    }

    public boolean reloading;

    public boolean shoot() {
        if (timeout <= 0 && ((resource != null && resource.quantity > 0) || !player)) {
            Weapon weapon = getSelected();
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

                timeout += 1 / weapon.getSpeed();
                stack++;
                if (player)
                    resource.quantity--;
                return true;
            } else if (magazine == 0) {
                reloading = true;

                reload();
                timeout += 20 / weapon.getSpeed(); // перезарядка
                return false;
            } else {
                stack = 0;
                timeout += 10 / weapon.getSpeed();
                return false;
            }
        } else return false;
    }


    void reload() {
        Weapon weapon = getSelected();
        if (weapon != null && (!player || (resource != null && resource.quantity > 0))) {
            int take = weapon.getBulletQuantity();
            if (player) {
                take = resource.quantity;
                if (weapon.getBulletQuantity() < take) {
                    take = weapon.getBulletQuantity();
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