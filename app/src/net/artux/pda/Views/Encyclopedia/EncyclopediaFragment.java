package net.artux.pda.Views.Encyclopedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;

public class EncyclopediaFragment extends Fragment {

    View view;
    WebView webView;

    public void load(String url){
        webView.loadUrl(url);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(view==null){
            view = inflater.inflate(R.layout.fragment_enc, container, false);
            webView = view.findViewById(R.id.webview);

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    view.loadUrl(url);
                    return true;
                }
            });

            if(getArguments()!=null){
                int id = getArguments().getInt("id");
                load("http://" + BuildConfig.URL + "/enc/" + id);
            }else{
                load("http://" + BuildConfig.URL + "/enc/" + "pistols");
            }
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        return view;
    }
}
