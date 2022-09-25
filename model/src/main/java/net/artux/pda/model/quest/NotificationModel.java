package net.artux.pda.model.quest;

import lombok.Data;

@Data
public class NotificationModel {

    private String title;
    private String message;
    private NotificationType type;

}
