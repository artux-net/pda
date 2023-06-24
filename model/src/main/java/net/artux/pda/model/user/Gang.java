package net.artux.pda.model.user;

/**
 * Группировки сталкеров с id и ссылкой на ресурс подписи
 */
public enum Gang {
    LONERS(0, "gang.loners"),
    BANDITS(1, "gang.bandits"),
    MILITARY(2, "gang.military"),
    LIBERTY(3, "gang.liberty"),
    DUTY(4, "gang.duty"),
    MONOLITH(5, "gang.monolith"),
    MERCENARIES(6, "gang.mercenaries"),
    SCIENTISTS(7, "gang.scientists"),
    CLEAR_SKY(8, "gang.clear_sky");

    private final int id;
    private final String titleId;

    Gang(int id, String titleId) {
        this.id = id;
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }

    public int getId() {
        return id;
    }

    public static Gang ofId(int id){
        for (Gang g : Gang.values()){
            if (g.getId() == id)
                return g;
        }
        return LONERS;
    }

}
