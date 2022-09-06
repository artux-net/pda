package net.artux.pda.model.quest;

import lombok.Data;

@Data
public class TransferModel {

    private int stageId;
    private String text;

    public TransferModel(int stageId, String text) {
        this.stageId = stageId;
        this.text = text;
    }
}
