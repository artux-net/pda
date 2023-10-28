package net.artux.pda.map.view.collection.table;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.root.FontManager;
import net.artux.pda.map.view.collection.table.item.ItemView;
import net.artux.pda.model.items.ItemModel;

import java.util.List;

public class ItemsHorizontalView extends Table {

    private final Label titleLabel;
    private final Label descLabel;
    private final Table itemsTable;
    private final AssetManager assetManager;
    private final LocaleBundle localeBundle;
    private OnItemClickListener onItemClickListener;
    private float weightSum;

    Label.LabelStyle titleStyle;
    Label.LabelStyle subtitleStyle;

    public ItemsHorizontalView(String title, Label.LabelStyle titleLabelStyle, Label.LabelStyle descLabelStyle, AssetsFinder assetsFinder, Skin skin) {
        super(skin);
        this.assetManager = assetsFinder.getManager();
        this.localeBundle = assetsFinder.getLocaleBundle();

        Table.debugCellColor = Colors.primaryColor;
        titleLabel = new Label(title, titleLabelStyle);
        descLabel = new Label("", descLabelStyle);
        add(titleLabel)
                .left()
                .spaceRight(10);
        add(descLabel)
                .left();
        add(new Label("  ", titleLabelStyle))
                .growX();
        row();

        FontManager fontManager = assetsFinder.getFontManager();
        titleStyle = fontManager.getLabelStyle(28, Color.WHITE);
        subtitleStyle = fontManager.getLabelStyle(22, Color.GRAY);

        itemsTable = new Table();
        itemsTable
                .align(Align.left | Align.top)
                .left()
                .defaults()
                .pad(5)
                .space(15);

        ScrollPane scrollPane = new ScrollPane(itemsTable, skin);
        scrollPane.setScrollingDisabled(false, true);

        add(scrollPane)
                .fillY()
                .growX()
                .uniformY()
                .colspan(2);
        row();
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void update(List<ItemModel> items) {
        weightSum = 0;
        itemsTable.clear();
        if (items.size() > 0)
            for (ItemModel itemModel : items) {
                if (itemModel.getQuantity() > 0) {
                    weightSum += itemModel.getQuantity() * itemModel.getWeight();

                    ItemView itemView = new ItemView(itemModel, titleStyle, subtitleStyle, assetManager);
                    itemView.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (onItemClickListener != null)
                                onItemClickListener.onTap(itemModel);
                        }
                    });
                    itemsTable.add(itemView)
                            .uniform()
                            .fill()
                            .minWidth(200);
                }
            }
        else
            itemsTable.add(new Label(localeBundle.get("main.empty"), subtitleStyle));
        descLabel.setText(localeBundle.get("user.info.weight", weightSum));
    }

    public void disableDesc() {
        descLabel.remove();
    }

    public float getWeightSum() {
        return weightSum;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}

