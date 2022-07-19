package net.artux.pda.map.models.user;

public class ParameterGdx {

    private String key;
    private int value;

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + key + ":" + value + '}';
    }
}
