package net.artux.pda.map.engine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.DataRepository
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.quest.story.StoryDataModel

class WeaponComponent : Component {
    var selected: WeaponModel? = null
        private set
    private var bulletModel: ItemModel? = null
    private lateinit var dataModel: StoryDataModel
    var magazine = 0
        private set
    private var stack = 0
    var timeout = 0f
    var player = false
    var shootLastFrame = false
    var shotSound: Sound? = null
        private set
    var reloadSound: Sound? = null
        private set
    private var assetManager: AssetManager

    constructor(dataRepository: DataRepository, assetManager: AssetManager) {
        this.assetManager = assetManager

        CoroutineScope(Dispatchers.Unconfined).launch {
            dataRepository.storyDataModelFlow.collect {
                updateData(it)
            }
        }
    }

    fun updateData(dataModel: StoryDataModel) {
        this.dataModel = dataModel
        player = true
        updateWeapon()
    }

    constructor(weaponModel: WeaponModel?, assetManager: AssetManager) {
        this.assetManager = assetManager
        player = false
        setWeaponModel(weaponModel)
        reload()
    }

    fun setWeaponModel(weaponModel: WeaponModel?) {
        selected = weaponModel
        if (weaponModel != null) {
            if (player) setBulletModel(dataModel.getItemByBaseId(weaponModel.bulletId))
            var reloadSoundName: String
            var shotSoundName: String
            if (weaponModel.type === ItemType.RIFLE) {
                reloadSoundName = rifleDefaultReloadSound
                shotSoundName = rifleDefaultShotSound
            } else {
                shotSoundName = pistolDefaultShotSound
                reloadSoundName = pistolDefaultReloadSound
            }
            val sounds = weaponModel.sounds
            if (sounds != null) {
                val type = weaponModel.type.name.lowercase()
                if (!sounds.reload.isNullOrBlank())
                    reloadSoundName = prefix + type + "/" + sounds.reload

                if (!sounds.shot.isNullOrBlank())
                    shotSoundName = prefix + type + "/" + sounds.shot
            }
            shotSound = assetManager.get(shotSoundName)
            reloadSound = assetManager.get(reloadSoundName)
            reload()
            reloading = false
        }
    }

    fun getBulletModel(): ItemModel? {
        return bulletModel
    }

    fun setBulletModel(item: ItemModel?) {
        if (item != null && selected!!.bulletId == item.baseId) {
            bulletModel = item
        }
    }

    var type = ItemType.RIFLE

    fun updateWeapon() {
        setWeaponModel(dataModel.getEquippedWearable(type) as WeaponModel?)
        if (selected == null) {
            type = if (type === ItemType.RIFLE) ItemType.PISTOL else ItemType.RIFLE
            setWeaponModel(dataModel.getEquippedWearable(type) as WeaponModel?)
        }
    }

    fun switchWeapons() {
        type = if (type === ItemType.RIFLE) ItemType.PISTOL else ItemType.RIFLE
        setWeaponModel(dataModel.getEquippedWearable(type) as WeaponModel?)
    }

    fun update(dt: Float) {
        if (timeout > 0) timeout -= dt
        if (timeout < 0) reloading = false

        /* if (resource != null && resource.getQuantity() < 1) {
            switchWeapons();
        }*/
    }

    var reloading = false
    fun shoot(): Boolean {
        if (timeout <= 0 && (bulletModel != null && bulletModel!!.quantity > 0 || !player)) {
            val weaponModel = selected
            val magazine = magazine
            if (stack < 4 && magazine > 0) {
                this.magazine--
                timeout += 1 / weaponModel!!.speed
                if (player) bulletModel!!.quantity = bulletModel!!.quantity - 1
                stack++
                shootLastFrame = true
            } else if (magazine == 0) {
                reload()
                timeout += 30 / weaponModel!!.speed // перезарядка
                shootLastFrame = false
            } else {
                stack = 0
                timeout += 20 / weaponModel!!.speed
                shootLastFrame = false
            }
        } else shootLastFrame = false
        return shootLastFrame
    }

    fun reload() {
        val weaponModel = selected
        if (weaponModel != null) {
            var take = weaponModel.bulletQuantity
            reloading = true
            if (player) {
                if (bulletModel != null) {
                    take = bulletModel!!.quantity
                    if (weaponModel.bulletQuantity < take) {
                        take = weaponModel.bulletQuantity
                    }
                } else take = 0
            }
            stack = 0
            magazine = take
        }
    }

    companion object {
        private const val prefix = "audio/sounds/weapons/"
        private const val rifleDefaultShotSound = prefix + "rifle/ak74_shot.ogg"
        private const val rifleDefaultReloadSound = prefix + "rifle/ak74_reload.ogg"
        private const val pistolDefaultShotSound = prefix + "pistol/pm_shot.ogg"
        private const val pistolDefaultReloadSound = prefix + "pistol/pm_reload.ogg"
    }
}