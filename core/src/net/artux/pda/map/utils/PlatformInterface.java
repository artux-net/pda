package net.artux.pda.map.utils;

import java.util.List;
import java.util.Map;

public interface PlatformInterface {

    void send(Map<String, String> data);

    void applyActions(Map<String, List<String>> actions);

    void restart();

    void rewardedAd();

    void debug(String msg);

    void toast(String msg);

    void error(String msg, Throwable t);
}
