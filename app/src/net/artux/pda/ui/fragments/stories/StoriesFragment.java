package net.artux.pda.ui.fragments.stories;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
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

import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.gdx.CoreStarter;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.Dialog;
import net.artux.pda.ui.fragments.quest.models.Stories;
import net.artux.pda.ui.fragments.quest.models.StoryItem;
import net.artux.pdalib.profile.items.GsonProvider;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import timber.log.Timber;

public class StoriesFragment extends BaseFragment implements StoriesAdapter.OnStoryClickListener {

    FragmentListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity()!=null && App.getDataManager().getMember().getData().getTemp().containsKey("currentStory")){
            Intent intent = new Intent(getActivity(), QuestActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else {
            navigationPresenter.setTitle(getResources().getString(R.string.map));
            navigationPresenter.setLoadingState(true);
            binding.list.setLayoutManager(new LinearLayoutManager(getContext()));

            StoriesAdapter adapter = new StoriesAdapter( StoriesFragment.this);
            binding.list.setAdapter(adapter);

            Stories cacheStories= GsonProvider.getInstance().fromJson(App.getDataManager().getString("stories"), Stories.class);
            if(cacheStories!=null){
                binding.list.setVisibility(View.VISIBLE);
                binding.viewMessage.setVisibility(View.GONE);
                adapter.setStories(cacheStories.get());
            }

            App.getRetrofitService().getPdaAPI().getStories().enqueue(new Callback<Stories>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Stories> call, Response<Stories> response) {
                    Stories stories = response.body();
                    navigationPresenter.setLoadingState(false);
                    if (((stories != null && cacheStories != null && stories.hashCode() != cacheStories.hashCode())
                            || (cacheStories == null && stories!=null))
                            && binding !=null) {
                        binding.list.setVisibility(View.VISIBLE);
                        binding.viewMessage.setVisibility(View.GONE);
                        App.getDataManager().setString("stories", GsonProvider.getInstance().toJson(stories));
                        adapter.setStories(stories.get());
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Stories> call, Throwable throwable) {
                    navigationPresenter.setLoadingState(false);
                    Timber.e(throwable);
                }
            });
        }
    }

    @Override
    public void onClick(int id) {
        if (id>-1) {
            Intent intent = new Intent(getActivity(), QuestActivity.class);
            intent.putExtra("story", id);
            if (getActivity() != null) {
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }else if(id==-1){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Input like that {story}:{chapter}:{stage}.");

            EditText input = new EditText(getActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
