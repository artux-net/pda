package net.artux.pda.model.map;

public enum Strength {
    WEAK("gang.strength.weak"),
    MIDDLE("gang.strength.middle"),
    STRONG("gang.strength.strong");

    private final String titleId;

    Strength(String titleId) {
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }
}