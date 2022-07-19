package net.artux.pda.models.quest.story;

public class ParameterModel {

    private String key;
    private int value;

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
