package net.artux.pda.model.quest

data class NotificationModel(
    var title: String,
    var message: String,
    var type: NotificationType = NotificationType.ALERT
){
    init {
        title.trim()
        message.trim()
    }
}