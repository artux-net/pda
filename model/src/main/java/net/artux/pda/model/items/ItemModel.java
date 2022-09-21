package net.artux.pda.model.items;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class ItemModel implements Serializable {

    protected UUID id;
    protected ItemType type;
    protected String icon;
    protected String title;
    protected int baseId;
    protected float weight;
    protected int price;
    protected int quantity;

}
