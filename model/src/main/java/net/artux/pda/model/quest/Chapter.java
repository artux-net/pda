package net.artux.pda.model.quest;

import java.util.List;

import lombok.Data;

@Data
public class Chapter {

    private List<Stage> stages;
    private List<Sound> music;

}
