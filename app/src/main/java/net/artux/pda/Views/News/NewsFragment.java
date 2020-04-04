package net.artux.pda.Views.News;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerView;
    MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

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
