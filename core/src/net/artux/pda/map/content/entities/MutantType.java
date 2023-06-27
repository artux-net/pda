package net.artux.pda.map.content.entities;

import net.artux.pda.map.ecs.battle.InfightingComponent;

public enum MutantType {

    DOG("mutant.dog", "textures/avatars/mutants/dog.jpg", false){
        @Override
        InfightingComponent getInfightingComponent() {
            return new InfightingComponent(10, 10, 1.5f);
        }
    },
    BOAR("mutant.boar", "textures/avatars/mutants/boar.jpg", false){
        @Override
        InfightingComponent getInfightingComponent() {
            return new InfightingComponent(10, 25, 2);
        }
    },
    //DEAD_STALKER("mutant.stalker", "avatarId", false),
    //CONTROLLER("mutant.controller", "textures/avatars/mutants/ctrl.jpg", true),
    BLOOD_HUNTER("mutant.blood_hunter", "textures/avatars/mutants/blood_hunter.jpg", true){
        @Override
        InfightingComponent getInfightingComponent() {
            return new InfightingComponent(9, 40, 3);
        }
    };

    private final String titleId;
    private final String avatarId;
    private final boolean isImportant;

    MutantType(String titleId, String avatarId, boolean isImportant) {
        this.titleId = titleId;
        this.avatarId = avatarId;
        this.isImportant = isImportant;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getIconId(){
        if (isImportant)
            return "textures/icons/entity/importantMutant.png";
        else return "textures/icons/entity/mutant.png";
    }

    public boolean isImportant() {
        return isImportant;
    }

    abstract InfightingComponent getInfightingComponent();
}
