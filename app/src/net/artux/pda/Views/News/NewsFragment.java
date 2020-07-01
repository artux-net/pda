package net.artux.pda.Views.News;

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
import net.artux.pda.activities.MainActivity;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {

    View mView;
    RecyclerView mRecyclerView;
    MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        navigationPresenter.setTitle(getResources().getString(R.string.news));
        if(mView==null){
            mainActivity = (MainActivity) getActivity();
            mView = inflater.inflate(R.layout.frament_news,container,false);
            mRecyclerView = mView.findViewById(R.id.newsRecycler);

            String urlString = "http://stalker-uc.ru/news/rss/";
            Parser parser = new Parser();
            parser.execute(urlString);
            parser.onFinish(new Parser.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Article> list) {
                    NewsAdapter newsAdapter = new NewsAdapter(mainActivity);
                    LinearLayoutManager manager = new LinearLayoutManager(mainActivity);
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
