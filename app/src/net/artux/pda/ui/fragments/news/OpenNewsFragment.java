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

import com.google.android.gms.ads.nativead.NativeAd;

import net.artux.pda.R;
import net.artux.pda.model.news.ArticleModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

public class OpenNewsFragment extends BaseFragment {

    private WebView content;
    private NativeAd oldAd;

    public static OpenNewsFragment of(ArticleModel articleModel) {
        OpenNewsFragment openNewsFragment = new OpenNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id", articleModel.getId());
        bundle.putString("title", articleModel.getTitle());
        bundle.putString("url", articleModel.getUrl());
        openNewsFragment.setArguments(bundle);
        return openNewsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_news, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String url = null;
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }

        content = view.findViewById(R.id.content);

        content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        content.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setDomStorageEnabled(true);
        content.getSettings().setLoadWithOverviewMode(true);
        content.getSettings().setUseWideViewPort(true);
        content.setBackgroundColor(Color.TRANSPARENT);

        content.setWebViewClient(new WebViewClient() {

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
        content.loadUrl(url);
    }

    @Override
    public void onDestroy() {
        content.destroy();
        super.onDestroy();
    }
}
