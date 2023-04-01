package net.artux.engine.graphics.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.artux.pda.map.utils.di.scope.PerGameMap;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class PostProcessing implements Disposable {

    private final HashMap<String, ShaderGroup> shaderGroups;
    private final FrameBuffer fbo;
    private final Batch batch;
    private final ShaderProgram defaultShader;
    public static float quality = 1f;

    @Inject
    public PostProcessing() {
        this.shaderGroups = new HashMap<>();
        this.batch = new SpriteBatch();
        defaultShader = batch.getShader();

        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frameBufferBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888);
        fbo = frameBufferBuilder.build();
    }


    public ShaderGroup loadShaderGroup(String key, List<Pair<ShaderProgram, ShaderSetup>> shaderPrograms) {
        List<ShaderContainer> containers = new LinkedList<>();
        for (Pair<ShaderProgram, ShaderSetup> e : shaderPrograms) {
            if (!e.getLeft().isCompiled())
                throw new GdxRuntimeException("Shader not compiled");
            FrameBuffer helpBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) (Gdx.graphics.getWidth() * quality),
                    (int) (Gdx.graphics.getHeight() * quality), false);
            containers.add(new ShaderContainer(e.getLeft(), helpBuffer, e.getRight()));
        }
        ShaderGroup group = ShaderGroup.of(containers);
        shaderGroups.put(key, group);
        Gdx.app.debug("PostProcessing", "Shader loaded: " + key);
        return group;
    }

    boolean removeGroup(String key) {
        ShaderGroup shaderGroup = shaderGroups.get(key);
        if (shaderGroup == null)
            return false;

        shaderGroup.dispose();
        shaderGroups.remove(key);
        return true;
    }

    public void begin() {
        fbo.begin();
    }

    public void end() {
        fbo.end();
    }

    private Texture processedTexture() {
        Texture texture = fbo.getColorBufferTexture();
        for (ShaderGroup group : shaderGroups.values()) {
            if (!group.enabled)
                continue;
            for (ShaderContainer container : group.getShaders()) {
                batch.setShader(container.shader);
                FrameBuffer frameBuffer = container.buffer;

                frameBuffer.begin();

                batch.begin();
                container.apply();
                batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                batch.end();

                frameBuffer.end();

                texture = frameBuffer.getColorBufferTexture();
            }
        }
        batch.setShader(defaultShader);

        return texture;
    }

    public void process() {
        Texture texture;
        if (shaderGroups.values().size() > 0)
            texture = processedTexture();
        else
            texture = fbo.getColorBufferTexture();

        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        batch.end();
    }

    public static class ShaderGroup implements Disposable {

        private final List<ShaderContainer> shaders;
        private boolean enabled;

        ShaderGroup(List<ShaderContainer> shaders) {
            this.shaders = shaders;
        }

        public List<ShaderContainer> getShaders() {
            if (enabled)
                return shaders;
            else return Collections.emptyList();
        }

        static ShaderGroup of(List<ShaderContainer> containers) {
            return new ShaderGroup(containers);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean contains(ShaderProgram shaderProgram) {
            for (ShaderContainer s : shaders) {
                if (s.shader == shaderProgram)
                    return true;
            }
            return false;
        }

        public void dispose() {
            for (ShaderContainer s : shaders) {
                s.buffer.dispose();
            }
        }
    }

    static class ShaderContainer {
        private final ShaderProgram shader;
        private final ShaderSetup setup;
        private final FrameBuffer buffer;

        ShaderContainer(ShaderProgram shader, FrameBuffer frameBuffer, ShaderSetup setup) {
            this.shader = shader;
            this.setup = setup;
            this.buffer = frameBuffer;
        }


        public void apply() {
            setup.apply(shader);
        }

    }

    public interface ShaderSetup {
        void apply(ShaderProgram shaderProgram);
    }

    @Override
    public void dispose() {
        fbo.dispose();
    }
}
