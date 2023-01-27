package net.artux.pda.model.items;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArmorModel extends WearableModel {

    private float thermalProtection;
    private float electricProtection;
    private float chemicalProtection;
    private float radioProtection;
    private float psyProtection;
    private float damageProtection;
    private float condition;

}
