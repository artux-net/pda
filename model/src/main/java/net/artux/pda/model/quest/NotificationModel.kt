package net.artux.pda.model.quest

data class NotificationModel(
    var title: String? = null,
    var message: String? = null,
    var type: NotificationType? = null
)