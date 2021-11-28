package net.artux.pda.ui.fragments.quest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.R;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.models.Transfer;
import net.artux.pdalib.Member;

import java.util.List;

public class Quest1Scene extends SceneFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mainText = view.findViewById(R.id.sceneText);
        TextView title = view.findViewById(R.id.sceneTitle);
        Button button = view.findViewById(R.id.okButton);

        Result<Member> memberResult = viewModel.getUserRepository().getCachedMember();
        if (memberResult instanceof Result.Success){
            Member member = ((Result.Success<Member>) memberResult).getData();
            mainText.setText(getTexts(stage, member).get(0).text);
            title.setText(getTitle());

            if (getActivity() != null) {
                if (!getBackground().equals(""))
                    ((QuestActivity) getActivity()).setBackground(getBackground());

                List<Transfer> transfers = getTransfers(stage, member);

                if (transfers.get(0).text != null && !transfers.get(0).text.equals(""))
                    button.setText(getTransfers(stage, member).get(0).text);
                else
                    button.setText(getActivity().getString(R.string.okay));

                button.setOnClickListener(v -> ((QuestActivity) getActivity()).getSceneController().showAd((transfers.get(0).stage_id)));
            }
        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
