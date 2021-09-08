package net.artux.pdalib.profile.items;

import java.util.Objects;

public class Item {

    public int id;
    public int type;
    public String icon;
    public String title;
    public float weight;
    public int price;
    public int quantity;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int priceToSell() {
        return (int) (0.9 * price);
    }

    @Override
    public String toString() {
        return GsonProvider.getInstance().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && type == item.type && Float.compare(item.weight, weight) == 0 &&
                price == item.price && quantity == item.quantity && Objects.equals(icon, item.icon) && Objects.equals(title, item.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, icon, title, weight, price, quantity);
    }
}

