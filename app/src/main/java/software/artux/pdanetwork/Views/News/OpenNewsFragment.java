package software.artux.pdanetwork.Views.News;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devilsoftware.pdanetwork.R;
import software.artux.pdanetwork.activities.MainActivity;
import com.prof.rssparser.Article;

public class
OpenNewsFragment extends Fragment {

    View mainView;
    Article mArticle;
    WebView content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(mainView==null){
            mainView = inflater.inflate(R.layout.fragment_open_news, container, false);

            content = mainView.findViewById(R.id.content);

            content.getSettings().setLoadWithOverviewMode(true);
            content.getSettings().setUseWideViewPort(true);
            content.getSettings().setDefaultFontSize(46);
            content.setBackgroundColor(Color.TRANSPARENT);

            content.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    ((MainActivity)getActivity()).setLoadingState(false);
                }
            });

            content.loadDataWithBaseURL("", "<font color=\"#808080\">" + mArticle.getContent() + "</font>","text/html", "UTF-8","");
        }

        return mainView;
    }


    void setArticle(Article article){
        mArticle = article;
    }
}
