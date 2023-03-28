package net.artux.pda.map.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.pathfinding.TiledNavigator;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

@PerGameMap
public class UIFrame extends WidgetGroup {

    private final Camera camera;
    private final Label counter;

    int w;
    int h;

    private final Table leftGroup;
    private final Table rightGroup;

    @Inject
    public UIFrame(AssetManager assetManager, Camera usualCamera, Camera uiCamera, TiledNavigator tiledNavigator, BitmapFont font) {
        super();
        this.camera = usualCamera;

        w = (int) uiCamera.viewportWidth;
        h = (int) uiCamera.viewportHeight;

        standardFrameSize = 30f;
        topFrameHeight = standardFrameSize * 2.5f;
        additionalSizes = topFrameHeight * 0.3f;
        headerBottomY = h - topFrameHeight + (additionalSizes) / 2;

        headerLeftX = standardFrameSize / 5;
        headerRightX = w - headerLeftX - 24;
        leftBarWidth = w - getHeaderLeftX() - standardFrameSize;

        float topHeaderOffset = (additionalSizes) / 2;
        headerHeight = h - topHeaderOffset - headerBottomY;

        leftGroup = new Table();
        leftGroup.align(Align.left);
        leftGroup.defaults()
                .pad(10)
                .height(headerHeight);

        rightGroup = new Table();
        rightGroup.defaults()
                .pad(10)
                .height(headerHeight)
                .width(headerHeight);
        rightGroup.align(Align.right | Align.center);

        Color backgroundColor = Colors.backgroundColor;
        Image image = new Image(Utils.getColoredRegion(w, (int) standardFrameSize, backgroundColor));
        addActor(image);

        image = new Image(Utils.getColoredRegion((int) getHeaderLeftX(), h, backgroundColor));
        addActor(image);

        image = new Image(Utils.getColoredRegion(w, (int) topFrameHeight + 1, backgroundColor));
        image.setPosition(0, h - topFrameHeight);
        addActor(image);

        image = new Image(Utils.getColoredRegion((int) standardFrameSize, h, backgroundColor));
        image.setPosition(w - standardFrameSize, 0);
        addActor(image);

        Color primaryColor = Colors.primaryColor;
        image = new Image(Utils.getColoredRegion((int) (w - getHeaderLeftX() * 2), 2, primaryColor));
        image.setPosition(getHeaderLeftX(), h - topFrameHeight + (additionalSizes) / 4);
        addActor(image);

        Table headerTable = new Table();
        headerTable.setPosition(headerLeftX, headerBottomY);
        headerTable.setSize((w - getHeaderLeftX() * 2), headerHeight);
        headerTable.setBackground(new TextureRegionDrawable(assetManager.get("textures/ui/title_background.png", Texture.class)));
        addActor(headerTable);

        headerTable.add(leftGroup)
                .left()
                .expandX();
        headerTable.add(rightGroup)
                .padRight(getHeaderLeftX())
                .fill();

        float frameOffset = standardFrameSize * 0.35f;

        Slider.SliderStyle style = new Slider.SliderStyle();
        style.knob = new TextureRegionDrawable(Utils.getColoredRegion(1, 1, primaryColor));
        style.knob.setMinHeight((int) (standardFrameSize - frameOffset));
        horizontalSlider = new Slider(0, tiledNavigator.getMapWidth(), 1, false, style);
        horizontalSlider.setSize(w - standardFrameSize - getHeaderLeftX(), standardFrameSize - additionalSizes * 2);
        horizontalSlider.setPosition(getHeaderLeftX(), additionalSizes);
        addActor(horizontalSlider);

        style = new Slider.SliderStyle();
        style.knob = Utils.getColoredDrawable(1, 1, primaryColor);
        style.knob.setMinWidth((int) (standardFrameSize - frameOffset));
        verticalSlider = new Slider(0, tiledNavigator.getMapHeight(), 1, true, style);

        verticalSlider.setSize(standardFrameSize - frameOffset, h - topFrameHeight - standardFrameSize);
        verticalSlider.setPosition(w - standardFrameSize + frameOffset / 2, standardFrameSize);
        addActor(verticalSlider);

        counter = new Label("0", new Label.LabelStyle(font, Color.ORANGE));
        counter.setPosition(w - standardFrameSize, 0);
        counter.setAlignment(Align.center);
        counter.setSize(standardFrameSize, standardFrameSize);
        addActor(counter);
    }

    Slider horizontalSlider;
    Slider verticalSlider;

    public Table getLeftHeaderTable() {
        return leftGroup;
    }

    public Table getRightHeaderTable() {
        return rightGroup;
    }

    float standardFrameSize;
    float topFrameHeight;
    float additionalSizes;
    float headerBottomY;

    float headerHeight;
    float headerLeftX;
    float headerRightX;
    float leftBarWidth;

    public float getHeaderLeftX() {
        return headerLeftX;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        counter.setText(String.valueOf(PlayerData.visibleEntities));
        horizontalSlider.setValue(GlobalData.cameraPosX);
        Vector3[] visibleCameraCorners = camera.frustum.planePoints;

        float widthK = (visibleCameraCorners[1].x - visibleCameraCorners[0].x) / GlobalData.mapWidth;
        float knobWidth = horizontalSlider.getWidth();
        if (widthK < 1)
            knobWidth *= widthK;

        horizontalSlider.getStyle().knob.setMinWidth(knobWidth);

        verticalSlider.setValue(GlobalData.cameraPosY);

        float heightK = (visibleCameraCorners[2].y - visibleCameraCorners[1].y) / GlobalData.mapHeight;
        float knobHeight = verticalSlider.getHeight();
        if (heightK < 1)
            knobHeight *= heightK;

        verticalSlider.getStyle().knob.setMinHeight(knobHeight);
    }

    public void setSlidersVisible(boolean value) {
        horizontalSlider.setVisible(value);
        verticalSlider.setVisible(value);
    }
}
