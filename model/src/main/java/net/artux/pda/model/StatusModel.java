package net.artux.pda.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class StatusModel implements Serializable {

    private boolean success;
    private String description;

    public StatusModel() {
    }

    public StatusModel(Throwable throwable) {
        success = false;
        description = throwable.getMessage();
    }
}