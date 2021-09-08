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

import androidx.activity.OnBackPressedCallback;
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

        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getString(R.string.enc));
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


        webView.clearView();
        if(getArguments()!=null){
            int id = getArguments().getInt("id");
            int type = getArguments().getInt("type");

            webView.loadUrl("https://" + BuildConfig.URL_API + "enc/" + type +"/"+id);
        }else{
            webView.loadUrl("https://" + BuildConfig.URL_API + "enc");
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

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                webView.clearView();
                if (webView.canGoBack())
                    webView.goBack();
                if (!webView.canGoBack())
                    setEnabled(false);
            }
        });
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
        if (data.containsKey("load"))
            webView.loadUrl(data.getString("load"));
    }


}
