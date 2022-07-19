package net.artux.pda.map.models.user;

public enum Gang {
    LONERS(0),
    BANDITS(1),
    MILITARY(2),
    LIBERTY(3),
    DUTY(4),
    MONOLITH(5),
    MERCENARIES(6),
    SCIENTISTS(7),
    CLEARSKY(8);

    private final int id;

    Gang(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
