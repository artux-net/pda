package net.artux.pda.map.engine.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import net.artux.pda.map.DataRepository
import net.artux.pda.map.engine.ecs.components.*
import net.artux.pda.map.utils.di.scope.PerGameMap
import net.artux.pda.map.view.LootMenu
import net.artux.pda.map.view.UserInterface
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@PerGameMap
class DeadCheckerSystem @Inject constructor(
    userInterface: UserInterface,
    lootMenu: LootMenu,
    dataRepository: DataRepository,
    assetManager: AssetManager,
    world: World
) : BaseSystem(
    Family.all(
        HealthComponent::class.java, BodyComponent::class.java
    ).get()
) {
    private val gameZone: Group
    private val lootMenu: LootMenu
    private val labelStyle: LabelStyle
    private var deadMessage = false
    private val dataRepository: DataRepository
    private val userInterface: UserInterface
    private val pm = ComponentMapper.getFor(
        BodyComponent::class.java
    )
    private val hm = ComponentMapper.getFor(
        HealthComponent::class.java
    )
    private val assetManager: AssetManager
    private val world: World

    init {
        gameZone = userInterface
        this.assetManager = assetManager
        this.userInterface = userInterface
        this.dataRepository = dataRepository
        labelStyle = userInterface.labelStyle
        this.world = world
        labelStyle.fontColor = Color.RED
        this.lootMenu = lootMenu
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val healthComponent = hm[entity]
            val bodyComponent = pm[entity]
            if (healthComponent.isDead) {
                val deadEntity = Entity()
                deadEntity.add(BodyComponent(bodyComponent.position, world))
                    .add(SpriteComponent(assetManager.get("textures/icons/entity/gray.png", Texture::class.java), 4f, 4f))
                if (entity !== player) {
                    val stalkerComponent = entity.getComponent(
                        StalkerComponent::class.java
                    )
                    deadEntity.add(InteractiveComponent("Обыскать: " + stalkerComponent.name, 5) {
                        lootMenu.updateBot(
                            stalkerComponent.name,
                            stalkerComponent.getAvatar(),
                            stalkerComponent.inventory
                        )
                        userInterface.stack.add(lootMenu)
                        engine.removeEntity(deadEntity)
                    })
                        .add(TimeComponent(
                            Instant.now().plus(1, ChronoUnit.MINUTES)
                        ) { engine.removeEntity(entity) })
                        .add(stalkerComponent)
                } else {
                    engine.removeEntity(player)
                }
                engine.addEntity(deadEntity)
                engine.removeEntity(entity)
            }
        }
        if (!isPlayerActive) {
            if (!deadMessage) {
                println("Dead message from :$this")
                dataRepository.applyActions(Collections.singletonMap("xp", listOf("-5")), false)
                val style = TextButtonStyle()
                style.font = labelStyle.font
                style.fontColor = Color.RED
                val textButton =
                    TextButton("Игра провалена! \n Для продолжения нажмите в любом месте.", style)
                textButton.setFillParent(true)
                textButton.align(Align.center)
                textButton.label.setAlignment(Align.center)
                userInterface.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        dataRepository.platformInterface.restart()
                        super.clicked(event, x, y)
                    }
                })
                gameZone.clearChildren()
                gameZone.addActor(textButton)
                deadMessage = true
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}
}