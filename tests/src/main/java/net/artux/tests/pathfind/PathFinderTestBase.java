package net.artux.tests.pathfind;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.tests.scene2d.CollapsableWindow;

public abstract class PathFinderTestBase {
    protected PathFinderAdapter container;
    public String testName;
    protected InputProcessor inputProcessor;
    protected CollapsableWindow detailWindow;

    public PathFinderTestBase (PathFinderAdapter container, String name) {
        this(container, name, null);
    }

    public PathFinderTestBase (PathFinderAdapter container, String testName, InputProcessor inputProcessor) {
        this.container = container;
        this.testName = testName;
        this.inputProcessor = inputProcessor;
    }

    public abstract void create ();

    public abstract void render ();

    public abstract void dispose ();

    public InputProcessor getInputProcessor () {
        return inputProcessor;
    }

    public void setInputProcessor (InputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    protected CollapsableWindow createDetailWindow (Table table) {
        CollapsableWindow window = new CollapsableWindow(this.testName, container.skin);
        window.row();
        window.add(table);
        window.pack();
        window.setX(container.stage.getWidth() - window.getWidth() + 1);
        window.setY(container.stage.getHeight() - window.getHeight() + 1);
        window.layout();
        window.collapse();
        return window;
    }


    protected void addSeparator (Table table) {
        Label lbl = new Label("", container.skin);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = container.skin.newDrawable("white");
        table.add(lbl).colspan(2).height(1).width(220).pad(5, 1, 5, 1);
    }
}
