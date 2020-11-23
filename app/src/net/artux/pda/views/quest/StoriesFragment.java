package net.artux.pda.views.quest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;
import net.artux.pda.activities.QuestActivity;
import net.artux.pda.app.App;
import net.artux.pda.views.quest.models.Stories;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoriesFragment extends BaseFragment implements StoriesAdapter.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationPresenter.setTitle(getResources().getString(R.string.quest));
        RecyclerView recyclerView = view.findViewById(R.id.list_quests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        App.getRetrofitService().getPdaAPI().getStories().enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
                Stories stories = response.body();
                if (stories!=null) {
                    StoriesAdapter adapter = new StoriesAdapter(stories.get(), StoriesFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

    }

    @Override
    public void onClick(int id) {
        if (id!=-1) {
            Intent intent = new Intent(getActivity(), QuestActivity.class);
            intent.putExtra("story", id);
            if (getActivity() != null) {
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Input like that {story}:{chapter}:{stage}.");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Load", (dialog, which) -> {
                String[] keys = input.getText().toString().split(":");
                if (keys.length==3){
                    try {
                        Intent intent = new Intent(getActivity(), QuestActivity.class);
                        int[] scs = {Integer.parseInt(keys[0]), Integer.parseInt(keys[1]), Integer.parseInt(keys[2])};
                        intent.putExtra("keys", scs);
                        if (getActivity() != null)
                            getActivity().startActivity(intent);
                    }catch (NumberFormatException e){
                        Toast.makeText(getActivity(), "Error, not numbers", Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(getActivity(), "Error, must be 3 keys", Toast.LENGTH_SHORT).show();

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}
