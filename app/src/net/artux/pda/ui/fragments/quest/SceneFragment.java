package net.artux.pda.ui.fragments.quest;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.artux.pda.databinding.FragmentNotificationBinding;
import net.artux.pda.ui.fragments.profile.ProfileHelper;
import net.artux.pda.viewmodels.ProfileViewModel;
import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.ui.fragments.quest.models.Text;
import net.artux.pda.ui.fragments.quest.models.Transfer;
import net.artux.pdalib.Checker;
import net.artux.pdalib.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SceneFragment extends Fragment implements StageNavigation.View{

    protected Stage stage;
    protected ProfileViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(ProfileViewModel.class);
        showMessage();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public HashMap<String, String> getData() {
        return stage.getData();
    }

    String getTitle(){
        if (stage.getTitle()!=null)
            return stage.getTitle();
        else
            return "";
    }

    String getBackground(){
        if (stage.getBackgroundUrl()!=null)
            return stage.getBackgroundUrl();
        else return "";

    }

    void showMessage(){
        if (stage!=null && !stage.getMessage().trim().equals("")) {

            FragmentNotificationBinding binding = FragmentNotificationBinding.inflate(getLayoutInflater());
            binding.notificationTitle.setText("Уведомление");
            binding.notificationContent.setText(stage.getMessage());

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(binding.getRoot());
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            dialog.show();

        }
    }

    List<Text> getTexts(Stage stage, Member member){
        List<Text> texts = new ArrayList<>();
        for (final Text text : stage.getText())
            if(Checker.check(text.condition, member)) {
                text.text = formatText(text.text, member);
                texts.add(text);
            }
        return texts;
    }

    List<Transfer> getTransfers(Stage stage, Member member){
        List<Transfer> transfers = new ArrayList<>();
        for (final Transfer transfer : stage.getTransfers())
            if(Checker.check(transfer.condition, member)) {
                transfer.text = formatText(transfer.text, member);
                transfers.add(transfer);
            }
        return transfers;
    }


    public String formatText(String text, Member member){
        return text.replaceAll("@name", member.getName())
                .replaceAll("@nickname", member.getNickname())
                .replaceAll("@money", String.valueOf(member.getMoney()))
                .replaceAll("@xp", String.valueOf(member.getXp()))
                .replaceAll("@login", member.getLogin())
                .replaceAll("@location", member.getLocation())
                .replaceAll("@group", ProfileHelper.getGroup(requireContext(), member.getGroup())
                .replaceAll("@pdaId", String.valueOf(member.getPdaId())));
    }
}
