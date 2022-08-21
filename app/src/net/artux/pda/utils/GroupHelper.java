package net.artux.pda.utils;

import android.content.Context;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.model.user.Gang;

public class GroupHelper {

    public static int getIconId(Gang gang) {
        return App.group_avatars[gang.getId()];
    }

    public static String getTitle(Gang gang, Context context) {
        return context.getResources().getStringArray(R.array.groups)[gang.getId()];
    }


}
