package net.artux.pda.map.engine.ecs.systems.player

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.pathfinding.own.Connection
import net.artux.engine.pathfinding.own.Digraph
import net.artux.engine.pathfinding.own.DijkstraPathFinder
import net.artux.pda.map.DataRepository
import net.artux.pda.map.engine.ecs.components.BodyComponent
import net.artux.pda.map.engine.ecs.components.PassivityComponent
import net.artux.pda.map.engine.ecs.components.map.QuestComponent
import net.artux.pda.map.engine.ecs.systems.BaseSystem
import net.artux.pda.map.engine.ecs.systems.SoundsSystem
import net.artux.pda.map.utils.Mappers
import net.artux.pda.map.utils.di.scope.PerGameMap
import net.artux.pda.map.view.blocks.MessagesPlane
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.map.Point
import net.artux.pda.model.quest.MissionModel
import net.artux.pda.model.quest.story.ParameterModel
import net.artux.pda.model.quest.story.StoryDataModel
import java.util.LinkedList
import java.util.stream.Collectors
import javax.inject.Inject

@PerGameMap
class MissionsSystem @Inject constructor(
    private val messagesPlane: MessagesPlane,
    assetManager: AssetManager,
    private val dataRepository: DataRepository,
    private val soundsSystem: SoundsSystem,
    private val cameraSystem: CameraSystem
) : BaseSystem(
    Family.all(
        BodyComponent::class.java, QuestComponent::class.java
    ).exclude(
        PassivityComponent::class.java
    ).get()
), Disposable {

    private val pm = ComponentMapper.getFor(
        BodyComponent::class.java
    )

    private val qcm = ComponentMapper.getFor(
        QuestComponent::class.java
    )

    private val pixelsPerMeter = 3f
    private val mapDigraph: Digraph<GameMap>
    private val pathFinder: DijkstraPathFinder<GameMap>
    private val missionUpdatedSound: Sound
    private var currentStoryDataModel: StoryDataModel
    private var activeMission: MissionModel? = null
    private var targetPosition: Vector2? = null

    init {
        missionUpdatedSound = assetManager.get("audio/sounds/pda/pda_objective.ogg")
        mapDigraph = buildSystemFromStory()
        pathFinder = DijkstraPathFinder()

        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                currentStoryDataModel = it
                updateData(dataRepository.storyDataModel)
            }
        }

        currentStoryDataModel = dataRepository.storyDataModel
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        setActiveMissionByName("")
        loadPreferences()
    }

    fun updateData(oldDataModel: StoryDataModel) {
        val paramSet = getUpdatedParams(oldDataModel)
        val paramArr = paramSet.toTypedArray()
        val updatedMissions = getMissions(paramArr)
        for (m in updatedMissions) {
            val checkpointModel = m.getCurrentCheckpoint(*paramArr)
            messagesPlane.addMessage(
                "textures/avatars/a0.png", "Задание обновлено: " + m.title,
                "Новая цель: " + checkpointModel!!.title, MessagesPlane.Length.SHORT
            )
            soundsSystem.playSound(missionUpdatedSound)
        }
    }

    fun getParams(dataModel: StoryDataModel): MutableSet<String> {
        return dataModel.parameters.stream()
            .map { obj: ParameterModel -> obj.key }.collect(Collectors.toSet())
    }

    fun getUpdatedParams(oldDataModel: StoryDataModel): Set<String> {
        val oldParams: Set<String> = getParams(oldDataModel)
        val params = getParams(currentStoryDataModel)
        params.removeIf { o: String -> oldParams.contains(o) }
        return params
    }

    val params: Array<String>
        get() {
            val dataModel = currentStoryDataModel
            return dataModel.parameters.stream()
                .map { obj: ParameterModel -> obj.key }.collect(Collectors.toSet()).toTypedArray()
        }
    val missions: List<MissionModel>
        get() = getMissions(params)

    fun getMissions(params: Array<String>): List<MissionModel> {
        return dataRepository.storyModel.getCurrentMissions(*params)
    }

    val points: List<QuestComponent>
        get() {
            val points = LinkedList<QuestComponent>()
            for (entity in entities) {
                points.add(qcm[entity])
            }
            return points
        }

    fun setActiveMissionByName(name: String) {
        val missionModels = missions
        for (m in missionModels) {
            if (m.name == name) {
                setActiveMission(activeMission)
            }
        }
        if (activeMission == null) {
            if (missionModels.isNotEmpty())
                setActiveMission(missionModels[0])
            else if (entities.size() > 0)
                setTargetPosition(pm[entities.first()].position)
        }
    }

    fun getActiveMission(): MissionModel? {
        return activeMission
    }

    fun setActiveMission(activeMission: MissionModel?) {
        this.activeMission = activeMission
        if (activeMission == null) return
        val currentCheckpoint = activeMission.getCurrentCheckpoint(*params)
        val chapter = currentCheckpoint?.chapter
        val stage = currentCheckpoint?.stage
        var found = false
        for (questEntity in entities) {
            val questComponent = qcm[questEntity]
            val position = pm[questEntity]
            if (questComponent.contains(chapter!!, stage!!)) {
                found = true
                setTargetPosition(position.position)
            }
        }
        if (!found) {
            val currentMap = dataRepository.gameMap
            val requiredMap = getRequiredMap(chapter!!, stage!!)
            if (requiredMap === currentMap || requiredMap == null) return
            val path = pathFinder.find(
                mapDigraph,
                mapDigraph.offer(currentMap),
                mapDigraph.offer(requiredMap)
            )
                ?: return
            val connection = path.connections.stream().findFirst()
            connection.ifPresent { gameMapConnection: Connection<GameMap> ->
                setTargetPosition(
                    Mappers
                        .vector2((gameMapConnection.userObject as Point).pos)
                )
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}

    private fun getRequiredMap(chapter: Int, stage: Int): GameMap? {
        val storyModel = dataRepository.storyModel
        for (map in storyModel.maps.values) {
            if (map.points != null)
                for (point in map.points!!) {
                    val currentData: Map<String, String> = point.data
                    val chapterString = currentData["chapter"]
                    val stageString = currentData["stage"]
                    if (chapterString != null && stageString != null) {
                        val stageId = stageString.toInt()
                        val chapterId = chapterString.toInt()
                        if (chapterId == chapter && stageId == stage) return map
                    }
                }
        }
        return null
    }

    private fun buildSystemFromStory(): Digraph<GameMap> {
        val storyModel = dataRepository.storyModel
        val mapDigraph = Digraph<GameMap>()
        for (map in storyModel.maps.values) {
            val node = mapDigraph.offer(map)
            for (point in map.points!!) {
                if (point.type == 7) {
                    val currentData: HashMap<String, String>? = point.data
                    val chapterString = currentData?.get("chapter")
                    val stageString = currentData?.get("stage")
                    var targetMap = currentData?.get("map") //with map
                    if (chapterString != null && stageString != null) {
                        //with chapter
                        val stage = stageString.toLong()
                        val data: Map<String, String>? = storyModel
                            .getChapter(chapterString)
                            ?.getStage(stage)?.data
                        if (data != null && data.containsKey("map")) targetMap = data["map"]
                    }
                    if (targetMap != null) {
                        val targetGameMap = storyModel.getMap(targetMap.toLong())
                        if (targetGameMap != null) {
                            val gameMapNode = mapDigraph.offer(targetGameMap)
                            val connection = node.addConnection(gameMapNode, 1)
                            connection.userObject = point
                        }
                    }
                }
            }
        }
        return mapDigraph
    }

    val position: Vector2
        get() {
            val playerPosition = pm[player]
            return playerPosition.position
        }
    val targetDistance: Int
        get() {
            if (targetPosition != null) {
                if (!targetPosition!!.isZero) {
                    return (position.dst(targetPosition) / pixelsPerMeter).toInt()
                }
            }
            return -1
        }
    val targetAngle: Double
        get() {
            if (targetPosition != null) {
                if (!targetPosition!!.isZero) {
                    val pos = position
                    return Math.atan2(
                        (
                                pos.y - targetPosition!!.y).toDouble(),
                        (
                                pos.x - targetPosition!!.x
                                ).toDouble()
                    ) * 180.0 / Math.PI + 90
                }
            }
            return 0.0
        }

    fun setTargetPosition(position: Vector2?) {
        if (position != null) {
            targetPosition = position
            cameraSystem.isDetached = true
            cameraSystem.camera.position.x = position.x
            cameraSystem.camera.position.y = position.y
        }
    }

    override fun dispose() {
        savePreferences()
    }

    fun savePreferences() {
        if (player != null) {
            val preferences = Gdx.app.getPreferences("missions")
            if (activeMission != null) preferences.putString("active", activeMission!!.name)
            preferences.flush()
        }
    }

    private fun loadPreferences() {
        if (player != null) {
            val preferences = Gdx.app.getPreferences("missions")
            val name = preferences.getString("active")
            setActiveMissionByName(name)
        }
    }
}