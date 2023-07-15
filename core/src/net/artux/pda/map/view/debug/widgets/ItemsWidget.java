package net.artux.pda.map.view.debug.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.commands.Commands;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.collection.table.ItemsTableView;
import net.artux.pda.map.view.collection.table.OnItemClickListener;
import net.artux.pda.map.view.Utils;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.items.WearableModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import javax.inject.Inject;

@PerGameMap
public class ItemsWidget extends Table {

    @Inject
    public ItemsWidget(Skin skin, DataRepository dataRepository, ItemsContainerModel itemsContainerModel, ItemsTableView tableView) {
        super(skin);

        defaults().top().left().align(Align.top).grow();
        add(tableView);
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));

        tableView.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onTap(@NotNull ItemModel itemModel) {
                int quantity = 10;
                if (itemModel instanceof WearableModel)
                    quantity = 1;

                String param = itemModel.baseId + ":" + quantity;

                dataRepository.applyActions(Collections.singletonMap(Commands.ADD, Collections.singletonList(param)), true);
            }

            @Override
            public void onLongPress(@NotNull ItemModel itemModel) {

            }
        });
        itemsContainerModel.getAll().forEach(itemModel -> itemModel.setQuantity(1));
        tableView.update(itemsContainerModel.getAll());
    }

}
