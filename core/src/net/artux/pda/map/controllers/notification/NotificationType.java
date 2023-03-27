package net.artux.pda.map.controllers.notification;

public enum NotificationType {
    ATTENTION("textures/ui/icons/ic_attention.png",
            "audio/sounds/pda/pda_news.ogg");


    private final String icon;
    private final String sound;

    NotificationType(String icon, String sound) {
        this.icon = icon;
        this.sound = sound;
    }

    public String getIcon() {
        return icon;
    }

    public String getSound() {
        return sound;
    }
}
