package net.artux.pda.map.model.entities;

public enum RelationType {

    ENEMY,
    NEUTRAL,
    FRIEND;

    public static RelationType by(int relation) {
        if (relation < -2)
            return ENEMY;
        else if (relation < 3)
            return NEUTRAL;
        else
            return FRIEND;
    }

}
