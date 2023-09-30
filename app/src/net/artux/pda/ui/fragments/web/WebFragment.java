package net.artux.pda.ui.fragments.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

public class WebFragment extends BaseFragment {

    protected WebView content;
    protected String url;

    public static WebFragment of(String title, String url) {
        WebFragment webFragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        webFragment.setArguments(bundle);

        return webFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null)
            url = getArguments().getString("url");

        content = view.findViewById(R.id.content);

        content.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setDomStorageEnabled(true);
        content.getSettings().setLoadWithOverviewMode(true);
        content.getSettings().setUseWideViewPort(true);
        content.setBackgroundColor(Color.TRANSPARENT);

        content.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request == null)
                    return false;
                String url = request.getUrl().toString();
                if (!url.startsWith("http"))
                    return false;
                else
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

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
