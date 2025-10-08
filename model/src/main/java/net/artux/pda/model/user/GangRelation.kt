package net.artux.pda.model.user

import java.io.Serializable

class GangRelation : Serializable {
    var bandits = 0
    var clearSky = 0
    var duty = 0
    var liberty = 0
    var loners = 0
    var mercenaries = 0
    var military = 0
    var monolith = 0
    var scientists = 0

    fun getFor(gang: Gang?): Int {
        return when (gang) {
            Gang.DUTY -> duty
            Gang.LONERS -> loners
            Gang.BANDITS -> bandits
            Gang.CLEAR_SKY -> clearSky
            Gang.LIBERTY -> liberty
            Gang.MERCENARIES -> mercenaries
            Gang.MILITARY -> military
            Gang.MONOLITH -> monolith
            else -> scientists
        }
    }
}