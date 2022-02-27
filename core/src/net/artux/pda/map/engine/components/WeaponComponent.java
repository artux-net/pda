package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Equipment;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Item;
import net.artux.pdalib.profile.items.Weapon;

public class WeaponComponent implements Component {

    private Armor armor;
    private Weapon weapon1;
    private Weapon weapon2;
    int selected = 0;

    int bullets_2;
    int bullets_1;

    int stack;

    float timeout;

    Member member;
    boolean player;

    public WeaponComponent(Member member) {
        Equipment equipment = member.getData().getEquipment();
        this.armor = equipment.getArmor();
        this.weapon1 = equipment.getSecondWeapon();
        this.weapon2 = equipment.getFirstWeapon();
        this.member = member;
        if (getSelected()!=null)
            setResource(member.getData().getItemById(getSelected().bullet_id));
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

    public void setResource(Item item){
        if (item != null && getSelected().bullet_id == item.id){
            resource = item;
        }
    }

    public int getMagazine(){
        int magazine;
        if (selected == 0)
            magazine = bullets_2;
        else
            magazine = bullets_1;
        return magazine;
    }

    public Weapon getSelected() {
        if (selected ==0 && weapon1!=null)
            return weapon1;
        else {
            selected = 1;
            return weapon2;
        }
    }

    public void setWeapon(Weapon weapon, int id) {
        if (id==0)
            weapon1 = weapon;
        else weapon2 = weapon;
    }

    public void update(float dt){
        if (timeout>0)
            timeout -= dt;
        if (timeout<0)
            reloading = false;

        if (resource != null && resource.quantity < 1){
            //swap
            Weapon another = getSelected()==weapon1 ? weapon2 : weapon1;
            if (another!=null){
                Item bullet = member.getData().getItemById(getSelected().bullet_id);
                if (bullet!=null && bullet.quantity > 0){
                    selected = selected == 0 ? 1 : 0;
                    setResource(bullet);
                }
            }
        }
    }

    public boolean reloading;

    public boolean shoot(){
        if (timeout <= 0  && ((resource!=null && resource.quantity > 0) || !player)) {
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

                timeout += 1 / weapon.speed;
                stack++;
                if (player)
                    resource.quantity--;
                return true;
            } else if (magazine == 0) {
                reloading = true;

                reload();
                timeout += 20 / weapon.speed; // перезарядка
                return false;
            } else {
                stack = 0;
                timeout += 10 / weapon.speed;
                return false;
            }
        }else return false;
    }


    void reload(){
        Weapon weapon = getSelected();
        if (weapon!=null && (!player || (resource!=null && resource.quantity>0))) {
            int take = weapon.bullet_quantity;
            if (player) {
                take = resource.quantity;
                if (weapon.bullet_quantity < take) {
                    take = weapon.bullet_quantity;
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