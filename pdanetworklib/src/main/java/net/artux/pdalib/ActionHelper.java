package net.artux.pdalib;

import net.artux.pdalib.profile.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ActionHelper {

    HashMap<String, List<String>> actions = new HashMap<>();

    public HashMap<String, List<String>> computeActions(Data before, Data after){
        //add and remove params
        /*after.getParameters().keys.stream().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(!before.getParameters().keys.contains(s)){
                    add("add_param", s);
                }
            }
        });
        before.getParameters().keys.stream().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(!after.getParameters().keys.contains(s)){
                    add("remove", s);
                }
            }
        });*/

        //

        return actions;
    }

    private void add(String action, String value){
        if (!actions.containsKey(action))
            actions.put(action, new ArrayList<String>());
        List<String> strings = actions.get(action);
        strings.add(value);
    }

}
