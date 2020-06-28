package net.artux.pda.Models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import net.artux.pda.R;
import net.artux.pda.app.App;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Profile {

    private String login;
    private String name;
    private byte admin;
    private byte blocked;
    private int group;
    private String avatar;
    private int pdaId;
    private int xp;
    private String location;
    private String registrationDate;
    private String data;
    private List<Integer> friends;
    private List<Integer> requests;


    public Profile(String login, String name, byte admin, byte blocked, int group, String avatar, int pdaId, int xp,
                   String location, String registrationDate, String data, List<Integer> friends, List<Integer> requests) {
        this.login = login;
        this.name = name;
        this.admin = admin;
        this.blocked = blocked;
        this.group = group;
        this.avatar = avatar;
        this.pdaId = pdaId;
        this.xp = xp;
        this.location = location;
        this.registrationDate = registrationDate;
        this.data = data;
        this.friends = friends;
        this.requests = requests;
    }


    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public byte getAdmin() {
        return admin;
    }

    public byte getBlocked() {
        return blocked;
    }

    public String getGroup(Context context) {
        return context.getResources().getStringArray(R.array.groups)[group];
    }

    private static boolean isInteger(String str) {
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

    public Drawable getAvatar(Context context) {
        if (isInteger(avatar))
            return context.getResources().getDrawable(App.avatars[Integer.parseInt(avatar)]);
        else
            //TODO: avatar with no id
            return null;
    }

    public int getPdaId() {
        return pdaId;
    }

    public String getRang(Context context) {
        return context.getResources().getStringArray(R.array.rangs)[xp];
    }

    public String getLocation() {
        return location;
    }

    public String getDays() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.US);
        Date past = null;
        try {
            past = df.parse(registrationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = daysBetween(past, now);

        return days + getDayAddition(days);
    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getDayAddition(int num) {
        int preLastDigit = num % 100 / 10;
        if (preLastDigit == 1)
        {
            return "дней";
        }

        switch (num % 10)
        {
            case 1:
                return "день";
            case 2:
            case 3:
            case 4:
                return "дня";
            default:
                return "дней";
        }
    }
}