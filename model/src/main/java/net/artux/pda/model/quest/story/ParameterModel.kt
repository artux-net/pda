package net.artux.pda.model.quest.story

import java.io.Serializable


data class ParameterModel(
    var key: String, var value: Int = 0
) : Serializable