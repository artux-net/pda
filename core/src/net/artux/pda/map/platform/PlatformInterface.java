package net.artux.pda.map.platform;

import java.util.HashMap;

public interface PlatformInterface {

    void send(HashMap<String,String> data);
    void debug(String msg);
    void error(String msg, Throwable t);
}
