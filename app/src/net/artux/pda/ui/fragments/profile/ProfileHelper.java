package net.artux.pda.ui.fragments.profile;

import android.content.Context;
import android.graphics.drawable.Drawable;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pdalib.Profile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileHelper {

    public static String getGroup(Profile profile, Context context) {
        return context.getResources().getStringArray(R.array.groups)[profile.getGroup()];
    }

    public static String getGroup(Context context, int group) {
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

    public static Drawable getAvatar(Profile profile, Context context) {
        if (isInteger(profile.getAvatar()))
            return context.getResources().getDrawable(App.avatars[Integer.parseInt(profile.getAvatar())]);
        else
            //TODO: avatar with no id
            return null;
    }

    public static Drawable getAvatar(Context context, String avatar) {
        if (isInteger(avatar))
            return context.getResources().getDrawable(App.avatars[Integer.parseInt(avatar)]);
        else
            //TODO: avatar with no id
            return null;
    }

    public static String getRang(Profile profile, Context context) {
        return context.getResources().getStringArray(R.array.rang)[rang(profile.getXp())];
    }

    public static String getRang(Context context, int xp){
        return context.getResources().getStringArray(R.array.rang)[rang(xp)];
    }

    private static int rang(int xp){
        if(xp<500)
            return 0;
        else if(xp<1500)
            return 1;
        else if(xp<3500)
            return 2;
        else if(xp<6000)
            return 3;
        else if(xp<9000)
            return 4;
        else if(xp<12000)
            return 5;
        else return 6;
    }

    public static String getDays(Profile profile) {
        Date now = new Date(), past = new Date(profile.getRegistrationDate());
        int days = daysBetween(past, now);

        return days + " " + getDayAddition(days);
    }

    public static String getDays(String date) {
        Date now = new Date(), past = new Date();
        DateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.US);
        try {
            past = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (past==null)
                past = new Date();
        }
        int days = daysBetween(past, now);

        return days + " " + getDayAddition(days);
    }

    private static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private static String getDayAddition(int num) {
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
