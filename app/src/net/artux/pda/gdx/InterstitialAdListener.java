package net.artux.pda.gdx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import net.artux.pda.ui.viewmodels.CommandViewModel;

import java.util.List;
import java.util.Map;

public class InterstitialAdListener implements InterstitialAdEventListener {

    private final CommandViewModel commandViewModel;

    public InterstitialAdListener(CommandViewModel commandViewModel) {
        this.commandViewModel = commandViewModel;
    }

    @Override
    public void onAdShown() {
        commandViewModel.processWithServer(Map.of("add", List.of("87:1")));
    }

    @Override
    public void onAdFailedToShow(@NonNull AdError adError) {

    }

    @Override
    public void onAdDismissed() {

    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onAdImpression(@Nullable ImpressionData impressionData) {

    }
}
