package net.artux.pda.ui.fragments.news;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prof.rssparser.Article;

import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

public class OpenNewsFragment extends BaseFragment {

    Article mArticle;
    WebView content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_news, container, false);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        content = view.findViewById(R.id.content);

        content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        content.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setDomStorageEnabled(true);
        content.getSettings().setLoadWithOverviewMode(true);
        content.getSettings().setUseWideViewPort(true);
        content.getSettings().setDefaultFontSize(46);
        content.setBackgroundColor(Color.TRANSPARENT);

        content.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                navigationPresenter.setLoadingState(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                navigationPresenter.setLoadingState(false);
            }
        });

        content.loadDataWithBaseURL("", "<font color=\"#808080\">" + mArticle.getContent() + "</font>","text/html", "UTF-8","");
    }

    void setArticle(Article article){
        mArticle = article;
    }
}
