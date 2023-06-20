package net.artux.pda.gdx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.rewarded.Reward;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;

import net.artux.pda.map.utils.PlatformInterface;

import java.util.List;
import java.util.Map;

public class VideoAdListener implements RewardedAdEventListener {

    private final PlatformInterface platformInterface;
    private final RewardedAd rewardedAd;

    public VideoAdListener(PlatformInterface platformInterface, RewardedAd rewardedAd) {
        this.platformInterface = platformInterface;
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
        platformInterface.applyActions(Map.of("money", List.of(String.valueOf(500))));
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
