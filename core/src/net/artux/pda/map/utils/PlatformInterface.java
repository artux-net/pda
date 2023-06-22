package net.artux.pda.map.utils;

import java.util.List;
import java.util.Map;

public interface PlatformInterface {

    void send(Map<String, String> data);

    void applyActions(Map<String, List<String>> actions);

    void restart();

    void exit();

}
