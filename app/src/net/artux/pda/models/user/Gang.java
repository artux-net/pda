package net.artux.pda.models.user;

import android.content.Context;

import net.artux.pda.R;
import net.artux.pda.app.App;

public enum Gang {
    LONERS(0),
    BANDITS(1),
    MILITARY(2),
    LIBERTY(3),
    DUTY(4),
    MONOLITH(5),
    MERCENARIES(6),
    SCIENTISTS(7),
    CLEARSKY(8);

    private final int id;

    Gang(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIconId() {
        return App.group_avatars[id];
    }

    public String getTitle(Context context) {
        return context.getResources().getStringArray(R.array.groups)[id];
    }

}
