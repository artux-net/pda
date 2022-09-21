package net.artux.pda.ui.fragments.stories;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import net.artux.pda.R;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.quest.StoriesContainer;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@AndroidEntryPoint
public class StoriesFragment extends BaseFragment implements StoriesAdapter.OnStoryClickListener {

    private FragmentListBinding binding;
    private QuestViewModel questViewModel;
    @Inject
    protected Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (questViewModel == null)
            questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);

        questViewModel.getStoryData().observe(getViewLifecycleOwner(), memberResult -> {
            if (memberResult.getCurrent() != null) {
                Intent intent = new Intent(getActivity(), QuestActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            } else loadStories();
        });
        questViewModel.updateData();
    }

    private void loadStories() {
        navigationPresenter.setTitle(getResources().getString(R.string.map));
        navigationPresenter.setLoadingState(true);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));

        StoriesAdapter adapter = new StoriesAdapter(StoriesFragment.this);
        binding.list.setAdapter(adapter);

        PDAApplication application = (PDAApplication) requireActivity().getApplication();

        //todo
        StoriesContainer cacheStoriesContainer = gson.fromJson(application.getDataManager().getString("stories"), StoriesContainer.class);
        if (cacheStoriesContainer != null) {
            binding.list.setVisibility(View.VISIBLE);
            binding.viewMessage.setVisibility(View.GONE);
            adapter.setStories(cacheStoriesContainer.getStories());
        }

        ((PDAApplication) getActivity().getApplication()).getOldApi().getStories().enqueue(new Callback<StoriesContainer>() {
            @Override
            public void onResponse(Call<StoriesContainer> call, Response<StoriesContainer> response) {
                StoriesContainer storiesContainer = response.body();
                navigationPresenter.setLoadingState(false);
                if (((storiesContainer != null && cacheStoriesContainer != null && storiesContainer.hashCode() != cacheStoriesContainer.hashCode())
                        || (cacheStoriesContainer == null && storiesContainer != null))
                        && binding != null) {
                    binding.list.setVisibility(View.VISIBLE);
                    binding.viewMessage.setVisibility(View.GONE);
                    PDAApplication application = (PDAApplication) requireActivity().getApplication();
                    application.getDataManager().setString("stories", gson.toJson(storiesContainer));
                    adapter.setStories(storiesContainer.getStories());
                }
            }

            @Override
            public void onFailure(Call<StoriesContainer> call, Throwable throwable) {
                navigationPresenter.setLoadingState(false);
                Timber.e(throwable);
            }
        });
    }

    @Override
    public void onClick(int id) {
        if (id > -1) {
            Intent intent = new Intent(getActivity(), QuestActivity.class);
            intent.putExtra("story", id);
            if (getActivity() != null) {
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        } else if (id == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Input like that {story}:{chapter}:{stage}.");

            EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Load", (dialog, which) -> {
                String[] keys = input.getText().toString().split(":");
                if (keys.length == 3) {
                    try {
                        Intent intent = new Intent(getActivity(), QuestActivity.class);
                        int[] scs = {Integer.parseInt(keys[0]), Integer.parseInt(keys[1]), Integer.parseInt(keys[2])};
                        intent.putExtra("keys", scs);
                        if (getActivity() != null)
                            getActivity().startActivity(intent);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Error, not numbers", Toast.LENGTH_SHORT).show();
                    }
                } else
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
