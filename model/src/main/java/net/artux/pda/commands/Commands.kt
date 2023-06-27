package net.artux.pda.commands

class Commands {

    companion object {
        const val ADD = "add"
        const val EXIT_STORY = "exitStory"
        const val FINISH_STORY = "finishStory"
        const val ITEM = "item"
        const val NOTE = "note"
        const val RESET = "reset"
        const val SET = "set"
        const val XP = "xp"
        const val MONEY = "money"
        const val STATE = "state"
        const val ITEM_CONDITION = "item_condition"
        const val REMOVE = "remove"

        val serverCommands = listOf(
            ADD,
            EXIT_STORY,
            FINISH_STORY,
            ITEM,
            NOTE,
            RESET,
            SET,
            XP,
            MONEY,
            ITEM_CONDITION,
            STATE,
            REMOVE
        )

        val commandsToSync = listOf(EXIT_STORY, FINISH_STORY, "syncNow")

    }


}