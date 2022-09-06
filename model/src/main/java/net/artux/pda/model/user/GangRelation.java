package net.artux.pda.model.user;

import lombok.Data;

@Data
public class GangRelation {

    private int bandits;
    private int clearSky;
    private int duty;
    private int liberty;
    private int loners;
    private int mercenaries;
    private int military;
    private int monolith;
    private int scientists;

    public int getFor(Gang gang) {
        switch (gang) {
            case DUTY:
                return duty;
            case LONERS:
                return loners;
            case BANDITS:
                return bandits;
            case CLEARSKY:
                return clearSky;
            case LIBERTY:
                return liberty;
            case MERCENARIES:
                return mercenaries;
            case MILITARY:
                return military;
            case MONOLITH:
                return monolith;
            default:
                return scientists;
        }
    }

}
