package net.artux.pda.map.content.entities;

import net.artux.pda.map.ecs.battle.InfightingComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.effects.Effect;

public enum MutantType {

    DOG("mutant.dog", "textures/avatars/mutants/dog.jpg", new String[]{
            "audio/sounds/mutant/dog/1.ogg",
            "audio/sounds/mutant/dog/2.ogg",
            "audio/sounds/mutant/dog/3.ogg",
            "audio/sounds/mutant/dog/4.ogg",
            "audio/sounds/mutant/dog/5.ogg",
            "audio/sounds/mutant/dog/6.ogg",
            "audio/sounds/mutant/dog/7.ogg",
            "audio/sounds/mutant/dog/8.ogg",
            "audio/sounds/mutant/dog/9.ogg",
    }, false) {
        @Override
        InfightingComponent getInfightingComponent() {
            return new InfightingComponent(10, 10, 1.5f, HealthComponent.DamageType.PSY);
        }
    },
    BOAR("mutant.boar", "textures/avatars/mutants/boar.jpg", new String[]{
            "audio/sounds/mutant/boar/1.ogg",
            "audio/sounds/mutant/boar/2.ogg",
            "audio/sounds/mutant/boar/3.ogg",
            "audio/sounds/mutant/boar/4.ogg"
    }, false) {
        @Override
        InfightingComponent getInfightingComponent() {
            return new InfightingComponent(10, 25, 2, HealthComponent.DamageType.SIMPLE);
        }
    },
    //DEAD_STALKER("mutant.stalker", "avatarId", false),
    CONTROLLER("mutant.controller", "textures/avatars/mutants/ctrl.jpg", new String[]{
            "audio/sounds/mutant/controller/1.ogg",
            "audio/sounds/mutant/controller/2.ogg"
    }, true){
        @Override
        InfightingComponent getInfightingComponent() {
            InfightingComponent infightingComponent = new InfightingComponent(200, 30, 10, HealthComponent.DamageType.PSY);
            infightingComponent.setEffect(Effect.LOST_MIND, 10);
            return infightingComponent;
        }
    },
    BLOOD_HUNTER("mutant.blood_hunter", "textures/avatars/mutants/blood_hunter.jpg", new String[]{
            "audio/sounds/mutant/blood_hunter/1.ogg",
            "audio/sounds/mutant/blood_hunter/2.ogg",
            "audio/sounds/mutant/blood_hunter/3.ogg"
    }, true) {
        @Override
        InfightingComponent getInfightingComponent() {
            InfightingComponent infightingComponent = new InfightingComponent(9, 40, 5, HealthComponent.DamageType.SIMPLE);
            infightingComponent.setEffect(Effect.STUCK, 1);
            return infightingComponent;
        }
    };

    private final String titleId;
    private final String avatarId;
    private final String[] attackSounds;
    private final boolean isImportant;

    MutantType(String titleId, String avatarId, String[] attackSounds, boolean isImportant) {
        this.titleId = titleId;
        this.avatarId = avatarId;
        this.attackSounds = attackSounds;
        this.isImportant = isImportant;
    }

    public String[] getAttackSounds() {
        return attackSounds;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getIconId() {
        if (isImportant)
            return "textures/icons/entity/importantMutant.png";
        else return "textures/icons/entity/mutant.png";
    }

    public boolean isImportant() {
        return isImportant;
    }

    abstract InfightingComponent getInfightingComponent();
}
