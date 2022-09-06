package net.artux.pda.model.quest;

import java.util.List;

import lombok.Data;

@Data
public class StageModel {

    private String title;
    private String content;
    private StageType type;
    private List<TransferModel> transfers;

}
