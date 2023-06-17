package net.artux.pda.common;

import java.util.List;
import java.util.Map;

public interface ActionHandler {

    void applyActions(Map<String, List<String>> actions);

}
