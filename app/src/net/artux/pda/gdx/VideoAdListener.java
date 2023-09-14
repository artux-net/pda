package net.artux.pda.gdx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.rewarded.Reward;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;

import net.artux.pda.ui.viewmodels.CommandViewModel;

import java.util.List;
import java.util.Map;

public class VideoAdListener implements RewardedAdEventListener {

    private final CommandViewModel commandViewModel;
    private final RewardedAd rewardedAd;

    public VideoAdListener(CommandViewModel commandViewModel, RewardedAd rewardedAd) {
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

    }

    @Override
    public void onAdDismissed() {

    }

    @Override
    public void onRewarded(@NonNull Reward reward) {
        commandViewModel.processWithServer(Map.of("add", List.of("84:1")));
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
