package net.artux.pda.views.encyclopedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;
import net.artux.pda.views.additional.AdditionalFragment;

public class EncyclopediaFragment extends BaseFragment {

    View view;
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        navigationPresenter.setTitle(getString(R.string.enc));
        AdditionalFragment additionalFragment = new AdditionalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("enc", 1);
        additionalFragment.setArguments(bundle);
        navigationPresenter.addAdditionalFragment(additionalFragment);

        if(view==null){
            view = inflater.inflate(R.layout.fragment_enc, container, false);
            webView = view.findViewById(R.id.webview);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    view.loadUrl(url);
                    return true;
                }
            });

            if(getArguments()!=null){
                int id = getArguments().getInt("id");
                webView.loadUrl("http://" + BuildConfig.URL + "/enc/" + id);
            }else{
                webView.loadUrl("http://" + BuildConfig.URL + "/enc/" + "pistols");
            }

            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        return view;
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
        if (data.containsKey("load"))
            webView.loadUrl(data.getString("load"));
    }
}
