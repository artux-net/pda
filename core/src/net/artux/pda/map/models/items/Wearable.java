package net.artux.pda.map.models.items;

public class Wearable extends Item {

    protected boolean isEquipped;

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }
}
