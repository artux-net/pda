package net.artux.pda.map.content.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.assets.AssetManager;

import net.artux.pda.map.content.ItemsGenerator;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.map.Strength;

import javax.inject.Inject;

/**
 * Отвечает за построение сущностей на карте
 */
@PerGameMap
public class StrengthUpdater {

    private final ApplicationLogger logger;
    private final ItemsGenerator itemsGenerator;
    private final AssetManager assetManager;
    private final ItemsContainerModel itemsContainerModel;

    @Inject
    public StrengthUpdater(ApplicationLogger logger,
                           ItemsContainerModel itemsContainerModel,
                           ItemsGenerator itemsGenerator, AssetManager assetManager) {
        this.logger = logger;
        this.itemsContainerModel = itemsContainerModel;
        this.itemsGenerator = itemsGenerator;
        this.assetManager = assetManager;
    }

    public Entity updateStalker(Entity entity, Strength strength) {
        int part = strength.ordinal();
        int of = Strength.values().length;
        WeaponModel weapon = (WeaponModel) itemsGenerator.getSpecificFromList(itemsContainerModel.getWeapons(), part, of);
        ArmorModel armor = (ArmorModel) itemsGenerator.getSpecificFromList(itemsContainerModel.getArmors(), part, of);

       /* ArmorModel armorModel = new ArmorModel();
        weapon.setType(ItemType.RIFLE);
        switch (strength) {
            case STRONG:
                weapon.setSpeed(random(13, 15));
                weapon.setDamage(random(9, 10));
                weapon.setPrecision(random(35, 45));
                weapon.setBulletQuantity(45);
                break;
            case MIDDLE:
                weapon.setSpeed(random(9, 15));
                weapon.setDamage(random(5, 10));
                weapon.setPrecision(random(25, 45));
                weapon.setBulletQuantity(45);
                break;
            default:
                weapon.setSpeed(random(5, 10));
                weapon.setDamage(random(3, 5));
                weapon.setPrecision(random(15, 25));
                weapon.setBulletQuantity(30);
                break;
        }*/

        if (entity.getComponent(WeaponComponent.class) == null)
            entity.add(new WeaponComponent(weapon, assetManager));
        else
            entity.getComponent(WeaponComponent.class).setWeaponModel(weapon);

        entity.getComponent(HealthComponent.class).setArmorModel(armor);
        return entity;
    }

}