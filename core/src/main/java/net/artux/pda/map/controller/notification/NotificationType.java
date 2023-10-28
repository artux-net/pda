package net.artux.pda.map.controller.notification;

public enum NotificationType {
    ATTENTION("textures/icons/notification/alert.png", "audio/sounds/pda/pda_news.ogg");

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
