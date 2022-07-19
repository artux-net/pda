package net.artux.pda.models.quest;

import net.artux.pda.models.user.UserModel;
import net.artux.pda.models.quest.story.StoryDataModel;

public class UserDataCompanion {

    private UserModel user;
    private StoryDataModel storyData;

    private UserDataCompanion(UserModel user, StoryDataModel storyData) {
        this.user = user;
        this.storyData = storyData;
    }

    public static UserDataCompanion of(UserModel userModel, StoryDataModel storyDataModel) {
        return new UserDataCompanion(userModel, storyDataModel);
    }

    public UserModel getUser() {
        return user;
    }

    public StoryDataModel getStoryData() {
        return storyData;
    }
}
