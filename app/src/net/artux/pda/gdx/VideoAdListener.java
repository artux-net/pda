package net.artux.pda.gdx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdError;
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

    public VideoAdListener(CommandViewModel commandViewModel) {
        this.commandViewModel = commandViewModel;
    }

    @Override
    public void onAdShown() {

    }

    @Override
    public void onAdFailedToShow(@NonNull AdError adError) {

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
    public void onAdImpression(@Nullable ImpressionData impressionData) {

    }

}
