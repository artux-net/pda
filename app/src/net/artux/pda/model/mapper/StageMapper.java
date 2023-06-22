package net.artux.pda.model.mapper;

import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.quest.NotificationModel;
import net.artux.pda.model.quest.NotificationType;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.Text;
import net.artux.pda.model.quest.Transfer;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Mapper
public interface StageMapper {

    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    default StageModel model(Stage stage, StoryDataModel dataCompanion) {
        StageModel stageModel = new StageModel();
        stageModel.setId(stage.getId());
        stageModel.setTitle(stage.getTitle());
        switch (stage.getTypeStage()){
            case 1:
                stageModel.setType(StageType.CHAPTER_OVER);
                break;
            case 7:
                stageModel.setType(StageType.DIALOG);
                break;
            default:
                stageModel.setType(StageType.USUAL);
                break;
        }

        stageModel.setContent(getText(stage, dataCompanion));
        stageModel.setTransfers(getTransfers(stage, dataCompanion));
        return stageModel;
    }

    default NotificationModel notification(Stage stage, StoryDataModel storyDataModel) {
        if (stage != null && stage.getMessage() != null && !stage.getMessage().trim().equals("")) {
            String title;
            String message = formatText(stage.getMessage(), storyDataModel);
            if (message.contains(":")) {
                String[] parts = message.split(":", 2);
                title = parts[0];
                message = parts[1];
            } else {
                title = "Уведомление";
            }
            NotificationType type =
                    stage.getTypeStage() == 0 ? NotificationType.ALERT : NotificationType.MESSAGE;
            return new NotificationModel(title, message, type);
        }
        return null;
    }


    default String getText(Stage stage, StoryDataModel dataCompanion) {
        List<Text> contentVariants = new ArrayList<>();
        for (Text text : stage.getTexts())
            if (QuestUtil.check(text.getCondition(), dataCompanion)) {
                text.setText(formatText(text.getText(), dataCompanion));
                contentVariants.add(text);
            }
        if (contentVariants.size() > 0)
            return contentVariants.get(0).getText().trim();
        else
            return "Недостижимые условия текста";
    }

    default List<TransferModel> getTransfers(Stage stage, StoryDataModel dataCompanion) {
        List<TransferModel> transfers = new LinkedList<>();
        for (Transfer transfer : stage.getTransfers())
            if (QuestUtil.check(transfer.getCondition(), dataCompanion)) {
                transfer.setText(formatText(transfer.getText(), dataCompanion).trim());
                transfers.add(new TransferModel(transfer.getStage(), transfer.getText()));
            }
        if (transfers.size() == 0)
            return Collections.singletonList(new TransferModel(-1, "Недостижимые условия переходов."));
        else
            return transfers;
    }


    default String formatText(String text, StoryDataModel userModel) {
        return text.replaceAll("@name", userModel.getName())
                .replaceAll("@nickname", userModel.getNickname())
                .replaceAll("@money", String.valueOf(userModel.getMoney()))
                .replaceAll("@xp", String.valueOf(userModel.getXp()))
                .replaceAll("@login", userModel.getLogin())
                .replaceAll("@pdaId", String.valueOf(userModel.getPdaId()));
    }

}
