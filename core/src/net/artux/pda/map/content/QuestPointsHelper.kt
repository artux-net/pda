package net.artux.pda.map.content

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.physics.box2d.World
import net.artux.pda.map.di.components.MapComponent
import net.artux.pda.map.ecs.interactive.ClickComponent
import net.artux.pda.map.ecs.interactive.InteractiveComponent
import net.artux.pda.map.ecs.interactive.map.ConditionComponent
import net.artux.pda.map.ecs.interactive.map.PointComponent
import net.artux.pda.map.ecs.interactive.map.QuestComponent
import net.artux.pda.map.ecs.interactive.map.TransferComponent
import net.artux.pda.map.ecs.physics.BodyComponent
import net.artux.pda.map.ecs.render.RenderSystem
import net.artux.pda.map.ecs.render.SpriteComponent
import net.artux.pda.map.utils.Mappers
import net.artux.pda.model.map.Point

object QuestPointsHelper {
    private val bcm = ComponentMapper.getFor(
        BodyComponent::class.java
    )

    @JvmStatic
    fun createQuestPointsEntities(coreComponent: MapComponent) {
        val engine = coreComponent.engine
        val (_, _, _, _, points) = coreComponent.dataRepository.gameMap
        for (point in points) {
            engine.addEntity(pointEntity(coreComponent, point, coreComponent.world))
        }
    }

    private fun pointEntity(coreComponent: MapComponent, point: Point, world: World): Entity {
        val engine = coreComponent.engine
        val assetManager = coreComponent.assetsManager
        val dataRepository = coreComponent.dataRepository
        val entity = Entity()
            .add(BodyComponent(Mappers.vector2(point.pos), world))
            .add(InteractiveComponent(point.name, point.type) {
                dataRepository.applyActions(point.actions)
                dataRepository.sendData(point.data)
            })
            .add(ClickComponent(23) {
                engine.getSystem(
                    RenderSystem::class.java
                )
                    .showText(point.name, Mappers.vector2(point.pos))
            })

        bcm[entity].body.setTransform(Mappers.vector2(point.pos), 0f)
        entity.add(ConditionComponent(point.condition))

        val pointComponent = PointComponent(point)
        if (pointComponent.type == PointComponent.Type.TRANSFER) entity.add(TransferComponent())
        val texture = getPointTexture(assetManager, pointComponent.type)
        val size = 23
        if (texture != null) {
            entity.add(SpriteComponent(texture, size.toFloat(), size.toFloat()))
            entity.add(pointComponent)
            if (!point.data["chapter"].isNullOrBlank() && !point.data["stage"].isNullOrBlank())
                entity.add(QuestComponent(point))
        }
        Gdx.app.debug("Points", "Point created at " + Mappers.vector2(point.pos) + " with name: " + point.name)
        return entity
    }

    @JvmStatic
    fun getPointTexture(assetManager: AssetManager, type: PointComponent.Type?): Texture? {
        return when (type) {
            PointComponent.Type.QUEST -> assetManager.get(
                "quest.png",
                Texture::class.java
            )

            PointComponent.Type.SELLER -> assetManager.get(
                "seller.png",
                Texture::class.java
            )

            PointComponent.Type.CACHE -> assetManager.get(
                "cache.png",
                Texture::class.java
            )

            PointComponent.Type.ADDITIONAL_QUEST -> assetManager.get(
                "quest1.png",
                Texture::class.java
            )

            PointComponent.Type.TRANSFER -> assetManager.get(
                "transfer.png",
                Texture::class.java
            )

            else -> null
        }
    }
}