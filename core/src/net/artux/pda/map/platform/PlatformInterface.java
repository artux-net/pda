package net.artux.pda.map.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PlatformInterface {

    void send(HashMap<String,String> data);
    void applyActions(Map<String, List<String>> actions);
    void restart();
    void debug(String msg);
    void toast(String msg);
    void error(String msg, Throwable t);
}
