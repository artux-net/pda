package net.artux.pda.ui.fragments.encyclopedia;

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

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;

public class EncyclopediaFragment extends BaseFragment {

    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdditionalFragment additionalFragment = new AdditionalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("enc", 1);
        additionalFragment.setArguments(bundle);

        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getString(R.string.enc));
            navigationPresenter.addAdditionalFragment(additionalFragment);
        }

        webView = view.findViewById(R.id.webview);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        if(getArguments()!=null){
            int id = getArguments().getInt("id");
            webView.loadUrl("https://" + BuildConfig.URL_API + "enc/" + id);
        }else{
            webView.loadUrl("https://" + BuildConfig.URL_API + "enc/" + "pistols");
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (navigationPresenter!=null)
                    navigationPresenter.setLoadingState(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (navigationPresenter!=null)
                    navigationPresenter.setLoadingState(false);
            }
        });
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
        if (data.containsKey("load"))
            webView.loadUrl(data.getString("load"));
    }
}
