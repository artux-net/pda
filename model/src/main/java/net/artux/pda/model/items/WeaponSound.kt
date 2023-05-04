package net.artux.pda.model.items

import java.io.Serializable

data class WeaponSound(
    var shot: String? = null,
    var reload: String? = null
) : Serializable