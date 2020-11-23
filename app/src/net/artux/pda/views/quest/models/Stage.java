package net.artux.pda.views.quest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

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
    @SerializedName("music")
    @Expose
    private int[] music;
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

    @SerializedName("data")
    @Expose
    private HashMap<String, String> data;

    public Integer getId() {
        return id;
    }

    public Integer getTypeStage() {
        return typeStage;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public int[] getMusics() {
        return music;
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

    public List<Text> getText() {
        return texts;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public HashMap<String, List<String>> getActions() {
        return actions;
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
