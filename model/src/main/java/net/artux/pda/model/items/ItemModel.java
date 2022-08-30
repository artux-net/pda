package net.artux.pda.model.items;

import java.util.UUID;

import lombok.Data;

@Data
public class ItemModel {

    protected UUID id;
    protected ItemType type;
    protected String icon;
    protected String title;
    protected int baseId;
    protected float weight;
    protected int price;
    protected int quantity;

}
