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

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoriesFragment extends BaseFragment implements StoriesAdapter.OnStoryClickListener {

    private FragmentListBinding binding;
    private QuestViewModel questViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationPresenter.setTitle(getResources().getString(R.string.map));


        StoriesAdapter adapter = new StoriesAdapter(StoriesFragment.this);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(adapter);

        if (questViewModel == null)
            questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);

        questViewModel.getStoryData().observe(getViewLifecycleOwner(), memberResult -> {
            if (memberResult.getCurrentState() != null) {
                StoryStateModel storyState = memberResult.getCurrentState();
                Intent intent = new Intent(getActivity(), QuestActivity.class);
                intent.putExtra("current", true);
                intent.putExtra("storyId", storyState.getStoryId());
                intent.putExtra("chapterId", storyState.getChapterId());
                intent.putExtra("stageId", storyState.getStageId());
                requireActivity().startActivity(intent);
                requireActivity().finish();
            } else questViewModel.updateStories();
        });

        questViewModel.getStoriesContainer().observe(getViewLifecycleOwner(), storiesContainer -> {
            navigationPresenter.setLoadingState(false);
            if (storiesContainer.getStories().size() > 0) {
                binding.list.setVisibility(View.VISIBLE);
                binding.viewMessage.setVisibility(View.GONE);
                adapter.setStories(storiesContainer.getStories());
            } else {
                binding.list.setVisibility(View.GONE);
                binding.viewMessage.setVisibility(View.VISIBLE);
            }
        });
        navigationPresenter.setLoadingState(true);
        questViewModel.updateData();
    }


    @Override
    public void onClick(int id) {
        if (id > -1) {
            Intent intent = new Intent(requireContext(), QuestActivity.class);
            StoryStateModel storyStateModel = questViewModel.getCachedData().getStateByStoryId(id);
            intent.putExtra("storyId", id);
            intent.putExtra("chapterId", storyStateModel.getChapterId());
            intent.putExtra("stageId", storyStateModel.getStageId());
            requireActivity().startActivity(intent);
            requireActivity().finish();
        } else if (id == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Формат ввода {storyId}:{chapterId}:{stageId}");

            EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Загрузить", (dialog, which) -> {
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
            builder.setNegativeButton("Закрыть", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
