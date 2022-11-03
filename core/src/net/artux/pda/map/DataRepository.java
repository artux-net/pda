package net.artux.pda.map;

import net.artux.pda.model.map.GameMap;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import org.apache.commons.lang3.SerializationUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataRepository {

    private PropertyChangeSupport propertyChangeSupport;
    private StoryDataModel oldStoryDataModel;
    private StoryDataModel storyDataModel;
    private GameMap gameMap;
    private UserModel userModel;

    private final PlatformInterface platformInterface;

    public DataRepository(PlatformInterface platformInterface) {
        this.platformInterface = platformInterface;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        setStoryDataModel(storyDataModel);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public StoryDataModel getStoryDataModel() {
        return storyDataModel;
    }

    public void setStoryDataModel(StoryDataModel storyDataModel) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(storyDataModel);
        }
        if (oldStoryDataModel == null) {
            oldStoryDataModel = storyDataModel;
        } else
            oldStoryDataModel = this.storyDataModel;
        this.storyDataModel = SerializationUtils.clone(storyDataModel);
        propertyChangeSupport.firePropertyChange("storyData", oldStoryDataModel, storyDataModel);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public PlatformInterface getPlatformInterface() {
        return platformInterface;
    }

    public void applyActions(Map<String, List<String>> actions) {
        HashMap<String, List<String>> summaryMap
                = new HashMap<>(QuestUtil.difference(oldStoryDataModel, storyDataModel));
        summaryMap.putAll(actions);
        platformInterface.applyActions(summaryMap);
    }
}
