package net.artux.pda.ui.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.news.ArticleModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.viewmodels.NewsViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewsFragment extends BaseFragment implements NewsAdapter.OnClickListener{

    private NewsAdapter adapter;
    private FragmentListBinding binding;
    private NewsViewModel newsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getResources().getString(R.string.news));
            navigationPresenter.setLoadingState(true);
        }
        if (newsViewModel == null)
            newsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);

        adapter = new NewsAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.list.setAdapter(adapter);

        newsViewModel.getArticles().observe(getViewLifecycleOwner(), new Observer<List<ArticleModel>>() {
            @Override
            public void onChanged(List<ArticleModel> articleModels) {
                if (articleModels.size() > 0){
                    binding.list.setVisibility(View.VISIBLE);
                    binding.viewMessage.setVisibility(View.GONE);
                    adapter.setNews(articleModels);
                } else {
                    binding.list.setVisibility(View.GONE);
                    binding.viewMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        newsViewModel.updateFromCache();
        newsViewModel.update();
    }

    @Override
    public void onDestroyView() {
        binding.list.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onClick(ArticleModel articleModel) {
        navigationPresenter.addFragment(ArticleFragment.of(articleModel), true);
    }
}
