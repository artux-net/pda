package net.artux.engine.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class MapBodyBuilder {

    // Пикселей на один тайл
    private static float ppt = 0;

    public static void buildShapes(TiledMap map, float pixels, World world) {
        ppt = pixels;
        MapObjects objects = map.getLayers().get("objects").getObjects();
        initObjects(objects, world, 0, 0);
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("tiles");

        tileLayer.setVisible(false);//todo remove

        for (int x = 0; x < tileLayer.getWidth(); x++) {
            for (int y = 0; y < tileLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                if (cell != null) {
                    TiledMapTile tile = cell.getTile();
                    if (tile.getObjects().getCount() > 0)
                        initObjects(tile.getObjects(), world, x * tileLayer.getTileWidth(), y * tileLayer.getTileHeight());
                }
            }
        }
    }

    private static void initObjects(MapObjects objects, World world, int xSwift, int ySwift) {
        BodyDef bd = new BodyDef();
        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;
            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof EllipseMapObject) {
                shape = getEllipse((EllipseMapObject) object);
            } else {
                continue;
            }

            bd.position.set(xSwift, ySwift);
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);

            body.createFixture(shape, 0);

            shape.dispose();
        }
    }

    private static Shape getEllipse(EllipseMapObject object) {
        Ellipse rectangle = object.getEllipse();

        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / ppt,
                (rectangle.y + rectangle.height * 0.5f) / ppt);

        if (rectangle.width == rectangle.height)
            return getCircle(new CircleMapObject(size.x, size.y, rectangle.height * 0.5f));

        Float rotation = (Float) object.getProperties().get("rotation");
        if (rotation == null)
            rotation = 0f;
        return createEllipse(size.x, size.y, rectangle.width, rectangle.height, rotation, 24);
    }

    private static ChainShape createEllipse(float positionX, float positionY, float width, float height, float rotation, int STEPS) {
        ChainShape ellipse = new ChainShape();
        Vector2[] verts = new Vector2[STEPS];

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        final float originX = -halfWidth;
        final float originY = halfHeight;

        rotation = -rotation;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        for (int i = 0; i < STEPS; i++) {
            float t = (float) (i * 2 * Math.PI) / STEPS;
            verts[i] = new Vector2((float) (halfWidth * Math.cos(t)), (float) (halfHeight * Math.sin(t)));

            float x = verts[i].x - originX;
            float y = verts[i].y - originY;

            if (rotation != 0) {
                float oldX = x;
                x = cos * x - sin * y;
                y = sin * oldX + cos * y;
            }

            verts[i].set(positionX + x + originX, positionY + y + originY);
        }

        ellipse.createLoop(verts);
        return ellipse;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();

        float halfWidth = rectangle.width * 0.5f;
        float halfHeight = rectangle.height * 0.5f;

        float[] rectangleVertices = new float[]{
                halfWidth, halfHeight,
                halfWidth, -halfHeight,
                -halfWidth, -halfHeight,
                -halfWidth, halfHeight
        };

        Polygon polygon = new Polygon(rectangleVertices);
        polygon.setPosition(rectangle.x + halfWidth, rectangle.y + halfHeight);
        Float rotation = (Float) rectangleObject.getProperties().get("rotation");
        if (rotation == null)
            rotation = 0f;
        polygon.setRotation(-rotation);
        polygon.setOrigin(-halfWidth, halfHeight);

        return getPolygon(new PolygonMapObject(polygon));
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / ppt);
        circleShape.setPosition(new Vector2(circle.x / ppt, circle.y / ppt));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / ppt;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / ppt;
            worldVertices[i].y = vertices[i * 2 + 1] / ppt;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }

}
