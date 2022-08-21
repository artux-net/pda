package net.artux.pda.model.profile;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class NoteModel implements Serializable {

    private UUID id;
    private String title;
    private String content;
    private Instant time;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
