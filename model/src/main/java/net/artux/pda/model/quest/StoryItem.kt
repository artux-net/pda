package net.artux.pda.model.quest

data class StoryItem(
    var id: Int = 0,
    var title: String,
    var icon: String? = null,
    var desc: String,
    var complete: Boolean = false,
    var needs: List<Int> = mutableListOf()
)