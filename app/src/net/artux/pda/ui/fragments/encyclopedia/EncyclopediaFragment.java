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

import net.artux.pda.R;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.utils.URLHelper;

import timber.log.Timber;

public class EncyclopediaFragment extends BaseFragment {

    private static final String BASE_ID = "baseId";
    private String lastUrl;
    private WebView webView;

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    public static EncyclopediaFragment of(ItemModel model) {
        EncyclopediaFragment encyclopediaFragment = new EncyclopediaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BASE_ID, model.baseId);
        encyclopediaFragment.setArguments(bundle);
        return encyclopediaFragment;
    }

    OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            webView.loadUrl("about:blank");
            if (webView.canGoBack())
                webView.goBack();
            if (!webView.canGoBack())
                setEnabled(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.enc));
        }

        webView = view.findViewById(R.id.webview);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        webView.loadUrl("about:blank");
        if (getArguments() != null) {
            int id = getArguments().getInt(BASE_ID);
            lastUrl = URLHelper.getApiUrl("enc/item/" + id);
        } else
            lastUrl = URLHelper.getApiUrl("enc");

        Timber.i("Load enc with baseId: %s", lastUrl);
        webView.loadUrl(lastUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (navigationPresenter != null)
                    navigationPresenter.setLoadingState(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (navigationPresenter != null)
                    navigationPresenter.setLoadingState(false);
                if (!lastUrl.equals(url))
                    callback.setEnabled(true);
            }
        });
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroy() {
        if (webView != null)
            webView.destroy();
        super.onDestroy();
    }
}
