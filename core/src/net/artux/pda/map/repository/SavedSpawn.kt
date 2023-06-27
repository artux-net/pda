package net.artux.pda.map.repository

import net.artux.pda.model.map.Strength
import net.artux.pda.model.user.Gang

data class SavedSpawn(val title: String, val gang: Gang, val strength: Strength, val stalkers: List<SavedStalker>)
