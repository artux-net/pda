package net.artux.pda.model.quest.mission

import java.io.Serializable

data class MissionModel(
    var title: String,
    var name: String,
    var checkpoints: List<CheckpointModel>
) : Serializable {

    fun hasParams(vararg params: String): Boolean {
        return getCurrentCheckpoint(*params) != null
    }

    fun getCurrentCheckpoint(vararg params: String): CheckpointModel? {
        for (checkpointModel in checkpoints) {
            if (checkpointModel.isActual(*params)) return checkpointModel
        }
        return null
    }
}