package net.artux.pda.model.mapper;

import android.content.Context;

import net.artux.pda.model.Checker;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.model.quest.UserDataCompanion;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.ui.fragments.quest.models.Text;
import net.artux.pda.ui.fragments.quest.models.Transfer;
import net.artux.pda.utils.GroupHelper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface StageMapper {

    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    default StageModel model(Stage stage, UserDataCompanion dataCompanion, Context context) {
        StageModel stageModel = new StageModel();
        stageModel.setTitle(stage.getTitle());
        if (stage.getTypeStage() == 1)
            stageModel.setType(StageType.CHAPTER_OVER);
        else
            stageModel.setType(StageType.USUAL);
        stageModel.setContent(getText(stage, dataCompanion, context));
        stageModel.setTransfers(getTransfers(stage, dataCompanion, context));
        return stageModel;
    }


    default String getText(Stage stage, UserDataCompanion dataCompanion, Context context) {
        List<Text> contentVariants = new ArrayList<>();
        for (Text text : stage.getText())
            if (Checker.check(text.condition, dataCompanion.getStoryData(), dataCompanion.getUser().getMoney())) {
                text.text = formatText(text.text, dataCompanion.getUser(), context);
                contentVariants.add(text);
            }
        return contentVariants.get(0).text;
    }

    default List<TransferModel> getTransfers(Stage stage, UserDataCompanion dataCompanion, Context context) {
        List<TransferModel> transfers = new ArrayList<>();
        for (Transfer transfer : stage.getTransfers())
            if (Checker.check(transfer.condition, dataCompanion.getStoryData(), dataCompanion.getUser().getMoney())) {
                transfer.text = formatText(transfer.text, dataCompanion.getUser(), context);
                transfers.add(new TransferModel(transfer.stage_id, transfer.text));
            }
        return transfers;
    }


    default String formatText(String text, UserModel userModel, Context context) {
        return text.replaceAll("@name", userModel.getName())
                .replaceAll("@nickname", userModel.getNickname())
                .replaceAll("@money", String.valueOf(userModel.getMoney()))
                .replaceAll("@xp", String.valueOf(userModel.getXp()))
                .replaceAll("@login", userModel.getLogin())
                .replaceAll("@group", GroupHelper.getTitle(userModel.getGang(), context))
                .replaceAll("@pdaId", String.valueOf(userModel.getPdaId()));
    }

}
