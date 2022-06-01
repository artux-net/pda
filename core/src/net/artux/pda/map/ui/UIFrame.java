package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.data.PlayerData;

public class UIFrame extends Group implements Disposable {

    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private final Label counter;

    private final Color primaryColor;
    private final Color backgroundColor;

    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();

    private final short[] squareTriangles = new short[]{
            0, 1, 3,
            1, 2, 3,
    };

    private final Table leftGroup;
    private final Table rightGroup;

    public UIFrame(Camera camera, BitmapFont font, Color primaryColor, Color backgroundColor) {
        super();
        this.primaryColor = primaryColor;
        this.backgroundColor = backgroundColor;
        this.camera = camera;
        shapeRenderer = new ShapeRenderer();

        float topHeaderOffset = (additionalSizes) / 2;
        headerHeight = h - topHeaderOffset - headerBottomY;

        Actor table = new Actor();
        table.setTouchable(Touchable.disabled);
        table.setSize(w * 10, h * 10);

        counter = new Label("0", new Label.LabelStyle(font, Color.ORANGE));
        counter.setPosition(w - frame, 0);
        counter.setAlignment(Align.center);
        counter.setSize(frame, frame);
        addActor(counter);

        float headerWidth = headerRightX - headerLeftX;

        leftGroup = new Table();
        leftGroup.setPosition(headerLeftX, headerBottomY);
        leftGroup.setSize(headerWidth / 2, headerHeight);
        leftGroup.align(Align.left | Align.center);
        leftGroup.defaults()
                .pad(10)
                .height(headerHeight);
        addActor(leftGroup);

        rightGroup = new Table();
        rightGroup.setPosition(headerLeftX + headerWidth / 2, headerBottomY);
        rightGroup.setSize(headerWidth / 2, headerHeight);
        rightGroup.defaults()
                .pad(10)
                .height(headerHeight)
                .width(headerHeight);
        rightGroup.align(Align.right | Align.center);

        addActor(rightGroup);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(primaryColor);
        pix.fill();
        textureSolid = new Texture(pix);
        pix.dispose();
        solidTextureRegion = new TextureRegion(textureSolid);

        PolygonRegion polyRegHeaderFrame = new PolygonRegion(solidTextureRegion,
                new float[]{
                        headerLeftX,
                        headerBottomY,
                        headerLeftX,
                        h - topHeaderOffset,
                        headerRightX,
                        h - topHeaderOffset,
                        w - headerLeftX,
                        h - topFrameHeight / 2,
                        w - headerLeftX,
                        headerBottomY
                },
                new short[]{
                        0, 3, 4,
                        0, 2, 3,
                        0, 1, 2
                });

        headerBarSprite = new PolygonSprite(polyRegHeaderFrame);
        bottomBarSprite = new PolygonSprite(polyRegHeaderFrame);
        rightBarSprite = new PolygonSprite(polyRegHeaderFrame);

        polyBatch = new PolygonSpriteBatch();
    }

    public Table getLeftHeaderTable() {
        return leftGroup;
    }

    public Table getRightHeaderTable() {
        return rightGroup;
    }


    float frame = h / 28f;
    float topFrameHeight = frame * 2;
    float additionalSizes = topFrameHeight * 0.3f;
    float headerBottomY = h - topFrameHeight + (additionalSizes) / 2;

    float headerHeight;
    float headerLeftX = frame / 5;
    float headerRightX = w - headerLeftX - (w / 80f);
    float leftBarWidth = w - getHeaderLeftX() - frame;

    PolygonSprite headerBarSprite;
    PolygonSprite bottomBarSprite;
    PolygonSprite rightBarSprite;

    PolygonSpriteBatch polyBatch;

    Texture textureSolid;
    TextureRegion solidTextureRegion;

    public float getHeaderLeftX() {
        return headerLeftX;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        counter.setText(Integer.toString(PlayerData.visibleEntities));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        //border frames
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.setProjectionMatrix(getStage().getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, getHeaderLeftX(), h);
        shapeRenderer.rect(0, 0, w, frame);
        shapeRenderer.rect(w - frame, 0, frame, h);
        shapeRenderer.rect(0, h - topFrameHeight, w, topFrameHeight);
        shapeRenderer.end();

        //line
        shapeRenderer.setProjectionMatrix(getStage().getBatch().getProjectionMatrix());
        shapeRenderer.setColor(primaryColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rectLine(getHeaderLeftX(), h - topFrameHeight + (additionalSizes) / 4, w - getHeaderLeftX(),
                h - topFrameHeight + (additionalSizes) / 4, 1);
        shapeRenderer.end();

        Vector3[] visibleCameraCorners = camera.frustum.planePoints;

        //bottom bar
        float widthK = (visibleCameraCorners[1].x - visibleCameraCorners[0].x) / GlobalData.mapWidth;
        float barWidth = leftBarWidth;
        if (widthK < 1)
            barWidth *= widthK;

        float bottomBarLeftX = getHeaderLeftX() + (GlobalData.cameraPosX / GlobalData.mapWidth) * leftBarWidth - barWidth * 0.5f;

        if (bottomBarLeftX < getHeaderLeftX()) {
            barWidth -= getHeaderLeftX() - bottomBarLeftX;
            bottomBarLeftX = getHeaderLeftX();
        }

        if (bottomBarLeftX + barWidth > w - frame) {
            barWidth -= bottomBarLeftX + barWidth - (w - frame);
        }

        polyBatch.begin();

        float bottomBarDownY = additionalSizes / 2;
        float bottomBarUpY = bottomBarDownY + frame - additionalSizes;

        PolygonRegion bottomBar = new PolygonRegion(solidTextureRegion,
                new float[]{
                        bottomBarLeftX,
                        bottomBarDownY,
                        bottomBarLeftX,
                        bottomBarUpY,
                        bottomBarLeftX + barWidth,
                        bottomBarUpY,
                        bottomBarLeftX + barWidth,
                        bottomBarDownY,
                }, squareTriangles);
        bottomBarSprite.setRegion(bottomBar);

        //right bar
        float heightK = (visibleCameraCorners[2].y - visibleCameraCorners[1].y) / GlobalData.mapHeight;

        float barHeight = h - frame - topFrameHeight;
        if (heightK < 1)
            barHeight *= heightK;

        float rightBarDownY = frame + (GlobalData.cameraPosY / GlobalData.mapHeight) * (h - frame - topFrameHeight) - barHeight * 0.5f;

        if (rightBarDownY < frame) {
            barHeight -= frame - rightBarDownY;
            rightBarDownY = frame;
        }

        if (rightBarDownY + barHeight > h - topFrameHeight) {
            barHeight -= rightBarDownY + barHeight - (h - topFrameHeight);
        }

        float rightBarLeftX = getHeaderLeftX() + leftBarWidth + additionalSizes / 2;
        float rightBarRightX = rightBarLeftX + frame - additionalSizes;


        PolygonRegion rightBar = new PolygonRegion(solidTextureRegion,
                new float[]{
                        rightBarLeftX,
                        rightBarDownY,
                        rightBarLeftX,
                        rightBarDownY + barHeight,
                        rightBarRightX,
                        rightBarDownY + barHeight,
                        rightBarRightX,
                        rightBarDownY
                }, squareTriangles);
        rightBarSprite.setRegion(rightBar);

        rightBarSprite.draw(polyBatch);
        bottomBarSprite.draw(polyBatch);
        headerBarSprite.draw(polyBatch);
        polyBatch.end();

        batch.begin();
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        textureSolid.dispose();
        polyBatch.dispose();
        shapeRenderer.dispose();
    }
}
