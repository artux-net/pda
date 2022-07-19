package net.artux.pda.map.engine;


import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.models.UserMessage;

public class MessageGenerator {

    private final ContentGenerator contentGenerator;

    public MessageGenerator(ContentGenerator contentGenerator) {
        this.contentGenerator = contentGenerator;
    }

    public UserMessage generateMessage() {
        return new UserMessage(contentGenerator.generateName(), contentGenerator.generateMessageContent(), String.valueOf(random(1, 30)));
    }
}
