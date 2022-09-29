package net.artux.pda.model.quest.story;

import java.io.Serializable;

import lombok.Data;

@Data
public class ParameterModel implements Serializable {

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

    @Override
    public String toString() {
        return "ParameterModel{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
