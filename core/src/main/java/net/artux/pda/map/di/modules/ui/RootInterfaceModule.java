package net.artux.pda.map.di.modules.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.pda.map.view.label.PDALabel;
import net.artux.pda.map.view.root.UserInterface;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


@Module(includes = TextModule.class)
public class RootInterfaceModule {

    @Provides
    @Named("assistantTable")
    public Table getAssistantTable(@Named("gameZone") Group gameZone) {
        Table assistantBlock = new Table();
        assistantBlock.setFillParent(true);
        assistantBlock.right().top();
        gameZone.addActor(assistantBlock);

        return assistantBlock;
    }

    @Provides
    @Named("gameZone")
    public Group getGameZone(UserInterface userInterface) {
        return userInterface.getGameZone();
    }

    @Provides
    @Named("hudTable")
    public Table getHudTable(@Named("gameZone") Group gameZone) {
        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.left().top();

        gameZone.addActor(hudTable);

        return hudTable;
    }

    @Provides
    @Named("targetLabel")
    public PDALabel pdaLabel(Skin skin){
        return new PDALabel(skin);
    }

    @Provides
    @Named("controlTable")
    public Table getControlTable(@Named("gameZone") Group gameZone) {
        Table controlBlock = new Table();
        controlBlock.setFillParent(true);
        controlBlock.right().bottom();
        controlBlock.defaults()
                .pad(10)
                .right();

        Color color = gameZone.getColor();
        color.a = 0.7f;
        controlBlock.setColor(color);
        color.a = 1f;
        gameZone.setColor(color);

        gameZone.addActor(controlBlock);
        return controlBlock;
    }

    @Provides
    @Named("joyTable")
    public Table getJoyTable(@Named("gameZone") Group gameZone) {
        Table table = new Table();
        table.setWidth(Gdx.graphics.getWidth()/3f);
        table.setFillParent(true);
        table.left().bottom();
        gameZone.addActor(table);
        return table;
    }

}
