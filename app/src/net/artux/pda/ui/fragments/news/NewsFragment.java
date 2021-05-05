package net.artux.pda.ui.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

import java.nio.charset.StandardCharsets;

import timber.log.Timber;

public class NewsFragment extends BaseFragment {

    NewsAdapter adapter;
    FragmentListBinding binding;

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

        adapter = new NewsAdapter(navigationPresenter);
        binding.list.setAdapter(adapter);

        String urlString = "https://pda-news.ucoz.net/news/rss/";

        Parser parser = new Parser.Builder()
                .charset(StandardCharsets.UTF_8)
                .build();
        parser.onFinish(new OnTaskCompleted() {

            @Override
            public void onTaskCompleted(Channel channel) {
                if (getActivity()!=null)
                    getActivity().runOnUiThread(() -> {
                        if (navigationPresenter!=null)
                            navigationPresenter.setLoadingState(false);
                        binding.list.setVisibility(View.VISIBLE);
                        binding.viewMessage.setVisibility(View.GONE);
                        adapter.setNews(channel.getArticles());
                    });
            }

            @Override
            public void onError(Exception e) {
                navigationPresenter.setLoadingState(false);
                Timber.tag("RSS").e(e);
            }
            
        });
        parser.execute(urlString);
    }
}
