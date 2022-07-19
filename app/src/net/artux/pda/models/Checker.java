package net.artux.pda.models;

import net.artux.pda.models.items.ItemModel;
import net.artux.pda.models.quest.story.StoryDataModel;

import java.util.HashMap;
import java.util.List;

public class Checker {

    public static boolean check(HashMap<String, List<String>> conditions, StoryDataModel storyDataModel, int userMoney) {
        if (conditions != null) {
            if (!conditions.keySet().isEmpty()) {
                HashMap<String, Integer> map = storyDataModel.getParametersMap();
                for (String condition : conditions.keySet()) {
                    switch (condition) {
                        case "has":
                            if (conditions.get(condition) != null)
                                for (String has : conditions.get(condition)) {
                                    if (!map.containsKey(has)
                                            && !containsItem(storyDataModel.getAllItems(), has))
                                        return false;
                                }
                            break;
                        case "!has":
                            if (conditions.get(condition) != null)
                                for (String has : conditions.get(condition)) {
                                    if (map.containsKey(has)
                                            || containsItem(storyDataModel.getAllItems(), has))
                                        return false;

                                }
                            break;
                        case "money>=":
                            if (conditions.get(condition) != null)
                                for (String money : conditions.get(condition)) {
                                    if (userMoney < Integer.parseInt(money))
                                        return false;
                                }
                            break;
                        case "money<":
                            if (conditions.get(condition) != null)
                                for (String money : conditions.get(condition)) {
                                    if (userMoney >= Integer.parseInt(money))
                                        return false;
                                }
                            break;

                    }
                }
            }
        }
        return true;
    }

    private static boolean containsItem(List<ItemModel> items, String sid) {
        if (isInteger(sid)) {
            int id = Integer.parseInt(sid);
            for (ItemModel item : items) {
                if (item.getBaseId() == id)
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
