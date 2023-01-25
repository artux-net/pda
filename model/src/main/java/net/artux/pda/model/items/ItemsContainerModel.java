package net.artux.pda.model.items;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ItemsContainerModel implements Serializable {

    private List<ArmorModel> armors;
    private List<WeaponModel> weapons;
    private List<MedicineModel> medicines;
    private List<DetectorModel> detectors;
    private List<ArtifactModel> artifacts;
    private List<ItemModel> bullets;
    private List<ItemModel> usual;

}
