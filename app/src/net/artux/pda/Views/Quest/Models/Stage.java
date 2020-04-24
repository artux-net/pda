package net.artux.pda.Views.Quest.Models;

import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stage {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type_stage")
    @Expose
    private Integer typeStage;
    @SerializedName("background_url")
    @Expose
    private String backgroundUrl;
    @SerializedName("music_id")
    @Expose
    private Object musicId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type_message")
    @Expose
    private Integer typeMessage;
    @SerializedName("texts")
    @Expose
    private List<Text> texts = null;
    @SerializedName("transfers")
    @Expose
    private List<Transfer> transfers = null;
    @SerializedName("actions")
    @Expose
    private HashMap<String, List<String>> actions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeStage() {
        return typeStage;
    }

    public void setTypeStage(Integer typeStage) {
        this.typeStage = typeStage;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public Object getMusicId() {
        return musicId;
    }

    public void setMusicId(Object musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(Integer typeMessage) {
        this.typeMessage = typeMessage;
    }

    public List<Text> getText() {
        return texts;
    }

    public void setText(List<Text> text) {
        this.texts = text;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public HashMap<String, List<String>> getActions() {
        return actions;
    }

    public void setActions(HashMap<String, List<String>> actions) {
        this.actions = actions;
    }
}
