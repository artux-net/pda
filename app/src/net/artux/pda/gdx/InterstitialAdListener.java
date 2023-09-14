package net.artux.pda.gdx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import net.artux.pda.ui.viewmodels.CommandViewModel;

import java.util.List;
import java.util.Map;

public class InterstitialAdListener implements InterstitialAdEventListener {

    private final CommandViewModel commandViewModel;
    private final InterstitialAd rewardedAd;

    public InterstitialAdListener(CommandViewModel commandViewModel, InterstitialAd rewardedAd) {
        this.commandViewModel = commandViewModel;
        this.rewardedAd = rewardedAd;
    }

    @Override
    public void onAdLoaded() {
        rewardedAd.show();
    }

    @Override
    public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {

    }

    @Override
    public void onAdShown() {
        commandViewModel.processWithServer(Map.of("add", List.of("87:1")));
    }

    @Override
    public void onAdDismissed() {

    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onLeftApplication() {

    }

    @Override
    public void onReturnedToApplication() {

    }

    @Override
    public void onImpression(@Nullable ImpressionData impressionData) {

    }
}
