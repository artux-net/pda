package net.artux.pda.models.quest;

public class TransferModel {

    private int stageId;
    private String text;

    public TransferModel(int stageId, String text) {
        this.stageId = stageId;
        this.text = text;
    }

    public int getStageId() {
        return stageId;
    }

    public String getText() {
        return text;
    }
}
