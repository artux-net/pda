package net.artux.pda.repositories

import net.artux.pda.model.quest.mission.MissionModel
import javax.inject.Inject

class MissionController @Inject constructor(
    var commandController: CommandController
) {

    lateinit var missions: MutableList<MissionModel>

    init {
        commandController.storyData.observeForever {

        }
    }

    fun check(){

    }

}