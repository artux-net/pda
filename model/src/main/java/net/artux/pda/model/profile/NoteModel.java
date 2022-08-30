package net.artux.pda.model.profile;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class NoteModel implements Serializable {

    private UUID id;
    private String title;
    private String content;
    private Instant time;

}
