package net.artux.pda.map;

import net.artux.pda.map.model.input.GameMap;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class DataRepository {

    private PropertyChangeSupport propertyChangeSupport;
    private StoryDataModel storyDataModel;
    private GameMap gameMap;
    private UserModel userModel;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


    public StoryDataModel getStoryDataModel() {
        return storyDataModel;
    }

    public void setStoryDataModel(StoryDataModel storyDataModel) {
        if (propertyChangeSupport == null)
            propertyChangeSupport = new PropertyChangeSupport(storyDataModel);
        StoryDataModel oldValue = this.storyDataModel;
        this.storyDataModel = storyDataModel;
        propertyChangeSupport.firePropertyChange("storyData", oldValue, storyDataModel);
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
}
