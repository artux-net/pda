package net.artux.pda.ui.viewmodels.event

data class ScreenDestination(val destination: Int){

    companion object {
        const val NONE = 0
        const val STORIES = 1
        const val PROFILE = 2
        const val NEWS = 3
        const val SETTINGS = 4
        const val NOTES = 5
        const val QUEST = 6
    }
}