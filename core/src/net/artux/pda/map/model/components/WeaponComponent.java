package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Equipment;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

public class WeaponComponent implements Component {

    private Armor armor;
    private Weapon weapon1;
    private Weapon weapon2;
    int weapon = 0;

    public WeaponComponent(Member member) {
        Equipment equipment = member.getData().getEquipment();
        this.armor = equipment.getArmor();
        this.weapon1 = equipment.getFirstWeapon();
        this.weapon2 = equipment.getSecondWeapon();
    }

    public WeaponComponent(Armor armor, Weapon weapon1, Weapon weapon2) {
        this.armor = armor;
        this.weapon1 = weapon1;
        this.weapon2 = weapon2;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Weapon getWeapon() {
        if (weapon==0)
            return weapon1;
        else return weapon2;
    }

    public void setWeapon(Weapon weapon, int id) {
        if (id==0)
            weapon1 = weapon;
        else weapon2 = weapon;
    }


}