package net.artux.pda.map.view.collection.table;


import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.pda.model.items.ItemModel;

import java.util.List;

import javax.inject.Inject;

public class ScrollItemsTableView extends Table {

    private final ItemsTableView itemsTable;

    @Inject
    public ScrollItemsTableView(Skin skin, ItemsTableView itemsTable) {
        super(skin);
        this.itemsTable = itemsTable;

        ScrollPane scrollPane = new ScrollPane(itemsTable, skin);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        add(scrollPane)
                .top()
                .grow()
                .colspan(3);
        row();
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        itemsTable.setOnClickListener(onItemClickListener);
    }

    public void update(List<ItemModel> items) {
        itemsTable.update(items);
    }

    public void disableDesc() {
        itemsTable.disableDesc();
    }

    public float getWeightSum() {
        return itemsTable.getWeightSum();
    }

    public void setTitle(String title) {
        itemsTable.setTitle(title);
    }
}

