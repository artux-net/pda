package net.artux.pda.map.engine.entities.model

import com.badlogic.gdx.math.MathUtils
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.battle.MoodComponent
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.map.SpawnModel
import net.artux.pda.model.user.Gang
import net.artux.pda.model.user.GangRelation
import java.util.Objects
import javax.inject.Inject

@PerGameMap
class GangRelations @Inject constructor() : ArrayList<GangRelation?>() {
    fun getMoodBy(gang: Gang, params: Set<String?>?): MoodComponent {
        return MoodComponent(gang, get(gang), params)
    }

    val relations: HashMap<Gang, Array<Int?>>
        get() {
            val map = HashMap<Gang, Array<Int?>>()
            for (gang in Gang.values()) {
                val relations = arrayOfNulls<Int>(9)
                for (gang1 in Gang.values()) {
                    relations[gang1.id] = get(gang)!!.getFor(gang1)
                }
                map[gang] = relations
            }
            return map
        }

    fun findEnemyByGang(gang: Gang): Gang? {
        for (potentialEnemy in Gang.values()) {
            if (getMoodBy(potentialEnemy, emptySet<String>()).isEnemy(
                    getMoodBy(
                        gang,
                        emptySet<String>()
                    )
                )
            ) return potentialEnemy
        }
        return null
    }

    fun findEnemyByGangFromCurrentMap(gang: Gang, map: GameMap): Gang? {
        val gangs = map.spawns
            .map(SpawnModel::group)
            .filter { obj: Gang? -> Objects.nonNull(obj) }
            .toList()

        for (potentialEnemy in gangs) {
            if (potentialEnemy != null && getMoodBy(potentialEnemy, emptySet<String>()).isEnemy(
                    getMoodBy(
                        gang,
                        emptySet<String>()
                    )
                )
            ) return potentialEnemy
        }
        return null
    }

    fun random(): Gang {
        return Gang.values()[MathUtils.random.nextInt(Gang.values().size)]
    }

    fun get(group: Gang): GangRelation? {
        return get(group.id)
    }

    fun getRelations(gang: Gang): Array<Int?> {
        val relations = arrayOfNulls<Int>(9)
        val relation = get(gang)
        for (gang1 in Gang.values()) {
            relations[gang1.id] = relation!!.getFor(gang)
        }
        return relations
    }
}
