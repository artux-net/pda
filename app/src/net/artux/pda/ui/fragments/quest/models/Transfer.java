package net.artux.pda.ui.fragments.quest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class Transfer {

    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("stage_id")
    @Expose
    public int stage_id;
    @SerializedName("condition")
    @Expose
    public HashMap<String, List<String>> condition;
    @SerializedName("actions")
    @Expose
    private HashMap<String, List<String>> actions;


}
