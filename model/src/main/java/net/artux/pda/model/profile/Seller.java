package net.artux.pda.model.profile;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ArtifactModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;

import java.util.ArrayList;
import java.util.List;

public class Seller {

    public int id;
    public String name;
    public String avatar;

    private final List<ArmorModel> armors = new ArrayList<>();
    private final List<WeaponModel> pistols = new ArrayList<>();
    private final List<WeaponModel> rifles = new ArrayList<>();
    private final List<ArtifactModel> artifacts = new ArrayList<>();
    private final List<ItemModel> items = new ArrayList<>();

    public List<ItemModel> getAllItems() {
        List<ItemModel> items = new ArrayList<>();
        items.addAll(armors);
        items.addAll(pistols);
        items.addAll(rifles);
        items.addAll(artifacts);
        items.addAll(this.items);
        return items;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", armors=" + armors +
                ", pistols=" + pistols +
                ", rifles=" + rifles +
                ", artifacts=" + artifacts +
                ", items=" + items +
                '}';
    }
}
