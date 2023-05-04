package net.artux.pda.model.quest

import java.io.Serializable

class MissionModel : Serializable {
    var title: String? = null
    var name: String? = null
    var checkpoints: List<CheckpointModel>? = null

    fun hasParams(vararg params: String): Boolean {
        return getCurrentCheckpoint(*params) != null
    }

    fun getCurrentCheckpoint(vararg params: String): CheckpointModel? {
        for (checkpointModel in checkpoints!!) {
            if (checkpointModel.isActual(*params)) return checkpointModel
        }
        return null
    }
}