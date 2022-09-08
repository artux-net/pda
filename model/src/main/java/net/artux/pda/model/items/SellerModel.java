package net.artux.pda.model.items;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class SellerModel {

    private Long id;
    private String name;
    private String icon;
    private String image;
    private List<ArmorModel> armors;
    private List<WeaponModel> weapons;
    private List<ArtifactModel> artifacts;
    private List<ItemModel> bullets;
    private List<MedicineModel> medicines;
    private List<DetectorModel> detectors;
    private Float buyCoefficient;
    private Float sellCoefficient;

    public List<ItemModel> getAllItems() {
        List<ItemModel> items = new LinkedList<>();
        items.addAll(weapons);
        items.addAll(armors);
        items.addAll(artifacts);
        items.addAll(medicines);
        items.addAll(detectors);
        items.addAll(bullets);

        return items;
    }

}
