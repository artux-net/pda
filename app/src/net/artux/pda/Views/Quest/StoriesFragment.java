package net.artux.pda.Views.Quest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.Views.Quest.Models.Stories;
import net.artux.pda.Views.Quest.Models.StoriesAdapter;
import net.artux.pda.activities.QuestActivity;
import net.artux.pda.app.App;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoriesFragment extends Fragment implements StoriesAdapter.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        Intent intent = new Intent(getActivity(), QuestActivity.class);
        intent.putExtra("story", id);
        if (getActivity()!=null)
            getActivity().startActivity(intent);
    }
}
