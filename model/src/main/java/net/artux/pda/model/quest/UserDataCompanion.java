package net.artux.pda.model.quest;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import lombok.Data;

@Data
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

}
