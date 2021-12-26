package net.artux.pda.ui.fragments.quest;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.artux.pda.R;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.models.Transfer;
import net.artux.pdalib.Member;

public class Quest0Scene extends SceneFragment{

    private LinearLayout sceneResponses;
    private ColorStateList colorStateList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mainText = view.findViewById(R.id.sceneText);
        sceneResponses = view.findViewById(R.id.sceneResponses);

        colorStateList = mainText.getTextColors();

        Result<Member> memberResult = viewModel.getUserRepository().getCachedMember();
        if (memberResult instanceof Result.Success){
            Member member = ((Result.Success<Member>) memberResult).getData();
            mainText.setText(getTexts(stage, member).get(0).text);
            setSceneResponses(member);
        }


        if (getActivity()!=null) {
            ((QuestActivity) getActivity()).setTitle(getTitle());
            if (!getBackground().equals(""))
                ((QuestActivity) getActivity()).setBackground(getBackground());
        }
    }

    private void setSceneResponses(Member member){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final Transfer transfer : getTransfers(stage, member)) {
            Button button = new Button(getContext());
            button.setLayoutParams(layoutParams);
            button.setPadding(10, 10, 10, 10);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setText(transfer.text);
            button.setAllCaps(false);
            button.setTextColor(colorStateList);
            if (getActivity()!=null) {
                button.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black_overlay));
                button.setOnClickListener(v ->
                        controller.chooseTransfer(transfer));
            }
            sceneResponses.addView(button);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
