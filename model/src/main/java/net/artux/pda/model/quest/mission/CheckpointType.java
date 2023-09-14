package net.artux.pda.model.quest.mission;

public enum CheckpointType {
    FIND_ITEM("textures/icons/notification/search.png"),
    KILL("textures/icons/notification/kill.png"),
    TRAVEL("textures/icons/notification/map.png");

    private final String iconId;

    CheckpointType(String iconId) {
        this.iconId = iconId;
    }

    public String getIconId() {
        return iconId;
    }
}
