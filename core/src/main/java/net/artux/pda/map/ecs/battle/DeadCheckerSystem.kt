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
import net.artux.pda.map.ecs.ai.EntityComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.physics.BodyComponent
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.ecs.interactive.InteractionSystem
import net.artux.pda.map.ecs.interactive.InteractiveComponent
import net.artux.pda.map.ecs.interactive.TimeComponent
import net.artux.pda.map.ecs.render.SpriteComponent
import net.artux.pda.map.ecs.systems.BaseSystem
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.view.view.window.LootWindow
import net.artux.pda.map.view.root.UserInterface
import net.artux.engine.utils.LocaleBundle
import java.util.*
import javax.inject.Inject

@PerGameMap
class DeadCheckerSystem @Inject constructor(
    userInterface: UserInterface,
    lootWindow: LootWindow,
    dataRepository: DataRepository,
    assetManager: AssetManager,
    world: World,
    localeBundle: LocaleBundle
) : BaseSystem(Family.all(HealthComponent::class.java, BodyComponent::class.java).get()) {

    private val gameZone: Group
    private val lootWindow: LootWindow
    private val labelStyle: LabelStyle
    private var deadMessage = false
    private val dataRepository: DataRepository
    private val userInterface: UserInterface
    private val localeBundle: LocaleBundle
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
        this.localeBundle = localeBundle
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
                    val entityComponent = entity.getComponent(
                        EntityComponent::class.java
                    )
                    deadEntity.add(
                        InteractiveComponent(
                            localeBundle.get("interaction.search", entityComponent.name),
                            5
                        ) {
                            lootWindow.updateBot(
                                entityComponent.name,
                                entityComponent.getAvatar(),
                                entityComponent.inventory
                            )
                            userInterface.stack.add(lootWindow)
                            engine.removeEntity(deadEntity)
                        })
                        .add(TimeComponent(
                            System.currentTimeMillis() + 60_000
                        ) { engine.removeEntity(entity) })
                        .add(entityComponent)
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
                    TextButton(localeBundle.get("main.gameOver"), style)
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