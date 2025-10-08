package net.artux.pda.model.quest

enum class NotificationType(
    val iconId: String
) {
    MESSAGE("textures/icons/notification/message.png"),
    ALERT("textures/icons/notification/alert.png"),
    KILL("textures/icons/notification/kill.png"),
    SEARCH("textures/icons/notification/search.png"),
    MAP("textures/icons/notification/map.png");


}