package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.Iterator;
import java.util.List;

public class WeaponComponent implements Component {

    private WeaponModel weaponModel;
    private ItemModel bulletModel;
    private final EntityBuilder entityBuilder;

    private StoryDataModel dataModel;

    private int bullets;
    private int stack;
    float timeout;

    boolean player;
    boolean shootLastFrame;

    public boolean isShootLastFrame() {
        return shootLastFrame;
    }

    public WeaponComponent(StoryDataModel dataModel, EntityBuilder entityBuilder) {
        this.entityBuilder = entityBuilder;
        updateData(dataModel);
    }

    public void updateData(StoryDataModel dataModel) {
        this.dataModel = dataModel;
        setWeaponModel((WeaponModel) dataModel.getCurrentWearable(ItemType.RIFLE));

        player = true;
        reload();
    }

    public WeaponComponent(WeaponModel weaponModel, EntityBuilder entityBuilder) {
        this.weaponModel = weaponModel;
        this.entityBuilder = entityBuilder;
        player = false;
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

                if (player)
                    bulletModel.setQuantity(bulletModel.getQuantity() - 1);
                stack++;
                shootLastFrame = true;
            } else if (magazine == 0) {
                reloading = true;

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

    public void sendBullet(Entity entity, Vector2 targetPosition) {
        entityBuilder.addBulletToEngine(entity, targetPosition, getSelected());
    }

    void reload() {
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