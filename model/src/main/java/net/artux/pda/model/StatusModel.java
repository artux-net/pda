package net.artux.pda.model;

import lombok.Data;

@Data
public class StatusModel {

    private boolean success;
    private String description;

    public StatusModel() {
    }

    public StatusModel(Throwable throwable) {
        success = false;
        description = throwable.getMessage();
    }
}