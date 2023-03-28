package net.artux.pda.map.content;


import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.chat.UserMessage;
import net.artux.pda.model.items.ItemModel;

import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class ContentGenerator {

    private final String[] names;
    private final String[] nicks;
    private final String[] messages;
    private final String[] actions;
    private final String[] locations;
    private final ItemsGenerator itemsGenerator;

    @Inject
    public ContentGenerator(ItemsGenerator itemsGenerator) {
        String folder = "templates/";
        names = readFile(folder + "names");
        nicks = readFile(folder + "nicks");
        messages = readFile(folder + "messages");
        locations = readFile(folder + "locations");
        actions = readFile(folder + "actions");
        this.itemsGenerator = itemsGenerator;
    }

    private String[] readFile(String file) {
        FileHandle handle = Gdx.files.internal(file);
        String text = handle.readString();
        return text.split("\\r?\\n");
    }

    public String generateName() {
        return names[random(0, names.length - 1)] + " " +
                nicks[random(0, nicks.length - 1)];
    }

    public String generateMessageContent() {
        return messages[random(0, messages.length - 1)];
    }

    public UserMessage generateMessage() {
        return new UserMessage(generateName(), generateMessageContent(), generateAvatar());
    }

    public String generateAvatar() {
        return "textures/avatars/a" + random(1, 30) + ".png";
    }

    public List<ItemModel> getRandomItems() {
        return itemsGenerator.getRandomItems();
    }
}
