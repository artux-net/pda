package net.artux.pdalib;

import net.artux.pdalib.profile.Data;
import net.artux.pdalib.profile.items.Item;

import java.util.HashMap;
import java.util.List;

public class Checker {

    public static boolean check(HashMap<String, List<String>> conditions, Member member){
        if (conditions!=null){
            if (!conditions.keySet().isEmpty()){
                Data data = member.getData();
                if(data!=null)
                    for(String condition : conditions.keySet()){
                        switch (condition){
                            case "has":
                                if (conditions.get(condition)!=null)
                                    for (String has : conditions.get(condition)){
                                        if (!data.parameters.keys.contains(has)
                                                && !data.parameters.values.containsKey(has)
                                                && !containsItem(data.getItems(), has))
                                            return false;
                                    }
                                break;
                            case "!has":
                                if (conditions.get(condition)!=null)
                                    for (String has : conditions.get(condition)){
                                        if (data.parameters.keys.contains(has)
                                                || data.parameters.values.containsKey(has)
                                                || containsItem(data.getItems(), has))
                                            return false;

                                    }
                                break;
                            case "money>=":
                                if (conditions.get(condition)!=null)
                                    for (String money : conditions.get(condition)){
                                        if (member.getMoney() < Integer.parseInt(money))
                                            return false;
                                    }
                                break;
                            case "money<":
                                if (conditions.get(condition)!=null)
                                    for (String money : conditions.get(condition)){
                                        if (member.getMoney() >= Integer.parseInt(money))
                                            return false;
                                    }
                                break;

                        }
                    } else return false;
            }
        }
        return true;
    }

    private static boolean containsItem(List<Item> items, String sid){
        if(isInteger(sid)){
            int id = Integer.parseInt(sid);
            for (Item item : items){
                if (item.getId()==id)
                    return true;
            }
        }

        return false;
    }


    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
