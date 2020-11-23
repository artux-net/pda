package net.artux.pda.views.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {

    View mView;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){
            if (navigationPresenter!=null)
                navigationPresenter.setTitle(getResources().getString(R.string.news));
            mView = inflater.inflate(R.layout.fragment_list,container,false);
            mRecyclerView = mView.findViewById(R.id.list);

            String urlString = "https://pda-news.ucoz.net/news/rss/";
            Parser parser = new Parser();
            parser.execute(urlString);
            parser.onFinish(new Parser.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Article> list) {
                    NewsAdapter newsAdapter = new NewsAdapter(navigationPresenter);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(manager);
                    newsAdapter.setNews(list);

                    mRecyclerView.setAdapter(newsAdapter);
                }

                @Override
                public void onError() {
                    Log.e("RSS", "error");
                }
            });

        }

        return mView;
    }
}
