package net.artux.pda.map.ecs.battle

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
import net.artux.pda.map.ecs.ai.StalkerComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.physics.BodyComponent
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.ecs.interactive.InteractionSystem
import net.artux.pda.map.ecs.interactive.InteractiveComponent
import net.artux.pda.map.ecs.interactive.TimeComponent
import net.artux.pda.map.ecs.render.SpriteComponent
import net.artux.pda.map.ecs.systems.BaseSystem
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.view.window.LootWindow
import net.artux.pda.map.view.root.UserInterface
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@PerGameMap
class DeadCheckerSystem @Inject constructor(
    userInterface: UserInterface,
    lootWindow: LootWindow,
    dataRepository: DataRepository,
    assetManager: AssetManager,
    world: World
) : BaseSystem(Family.all(HealthComponent::class.java, BodyComponent::class.java).get()) {

    private val gameZone: Group
    private val lootWindow: LootWindow
    private val labelStyle: LabelStyle
    private var deadMessage = false
    private val dataRepository: DataRepository
    private val userInterface: UserInterface
    private val pm = ComponentMapper.getFor(BodyComponent::class.java)
    private val hm = ComponentMapper.getFor(HealthComponent::class.java)
    private val assetManager: AssetManager
    private val world: World

    init {
        this.assetManager = assetManager
        this.userInterface = userInterface
        this.dataRepository = dataRepository
        this.lootWindow = lootWindow
        this.world = world
        labelStyle = userInterface.labelStyle
        labelStyle.fontColor = Color.RED

        gameZone = userInterface
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val healthComponent = hm[entity]
            val bodyComponent = pm[entity]
            if (healthComponent.isDead()) {

                val deadEntity = Entity()
                deadEntity.add(
                    BodyComponent(
                        bodyComponent.position,
                        world
                    )
                )
                    .add(
                        SpriteComponent(
                            assetManager.get("textures/icons/entity/gray.png", Texture::class.java),
                            4f,
                            4f
                        )
                    )
                if (entity !== player) {
                    val stalkerComponent = entity.getComponent(
                        StalkerComponent::class.java
                    )
                    deadEntity.add(
                        InteractiveComponent(
                            "Обыскать: " + stalkerComponent.name,
                            5
                        ) {
                            lootWindow.updateBot(
                                stalkerComponent.name,
                                stalkerComponent.getAvatar(),
                                stalkerComponent.inventory
                            )
                            userInterface.stack.add(lootWindow)
                            engine.removeEntity(deadEntity)
                        })
                        .add(TimeComponent(
                            Instant.now().plus(1, ChronoUnit.MINUTES)
                        ) { engine.removeEntity(entity) })
                        .add(stalkerComponent)
                } else {
                    engine.getSystem(InteractionSystem::class.java).setProcessing(false)
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