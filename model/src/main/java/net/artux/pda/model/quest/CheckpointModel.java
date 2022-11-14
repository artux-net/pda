package net.artux.pda.model.quest;

import java.io.Serializable;

import lombok.Data;

@Data
public class CheckpointModel implements Serializable {
    private String parameter;
    private String title;
    private Integer chapter;
    private Integer stage;

    public boolean isActual(String... params){
        for (String param : params){
            if (param.equals(parameter))
                return true;
        }
        return false;
    }
}
