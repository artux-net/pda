package net.artux.pda.map.repository;


import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import net.artux.pda.model.UserMessage;

import javax.inject.Inject;

public class ContentGenerator {

    private final String folder = "templates/";
    private final String[] names;
    private final String[] nicks;
    private final String[] messages;
    private final String[] actions;
    private final String[] locations;

    @Inject
    public ContentGenerator() {
        names = readFile(folder + "names");
        nicks = readFile(folder + "nicks");
        messages = readFile(folder + "messages");
        locations = readFile(folder + "locations");
        actions = readFile(folder + "actions");
    }

    private String[] readFile(String file) {
        FileHandle handle = Gdx.files.internal(file);
        String text = handle.readString();
        return text.split("\\r?\\n");
    }

    public String generateName(){
        return names[random(0, names.length-1)] + " " +
                nicks[random(0, nicks.length-1)];
    }

    public String generateMessageContent(){
        return messages[random(0, messages.length-1)];
    }

    public UserMessage generateMessage(){
        return new UserMessage(generateName(), generateMessageContent(), String.valueOf(random(1, 30)));
    }
}
