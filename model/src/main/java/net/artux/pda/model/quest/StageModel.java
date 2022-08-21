package net.artux.pda.model.quest;

import java.util.List;

public class StageModel {

    private String title;
    private String content;
    private StageType type;
    private List<TransferModel> transfers;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StageType getType() {
        return type;
    }

    public void setType(StageType type) {
        this.type = type;
    }

    public List<TransferModel> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<TransferModel> transfers) {
        this.transfers = transfers;
    }
}
