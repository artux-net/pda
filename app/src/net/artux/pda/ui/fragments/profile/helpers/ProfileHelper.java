package net.artux.pda.ui.fragments.profile.helpers;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.models.user.ProfileModel;

import java.util.Date;

public class ProfileHelper {

    public static String getGroup(ProfileModel profileModel, Context context) {
        return context.getResources().getStringArray(R.array.groups)[profileModel.getGroup()];
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

    public static void setAvatar(ImageView imageView, String avatar) {
        Context context = imageView.getContext();
        if (isInteger(avatar) && context.getResources() != null)
            Glide.with(context)
                    .load(Uri.parse("file:///android_asset/avatars/a" + (Integer.parseInt(avatar) + 1) + ".png"))
                    .into(imageView);
        else {
            if (!avatar.contains("http")) {
                avatar = "https://" + BuildConfig.URL + "/" + avatar;
            }

            Glide.with(context).asDrawable().load(avatar).into(imageView);
        }
    }

    public static String getRang(ProfileModel profileModel, Context context) {
        return context.getResources().getStringArray(R.array.rang)[rang(profileModel.getXp())];
    }

    public static String getRang(Context context, int xp) {
        return context.getResources().getStringArray(R.array.rang)[rang(xp)];
    }

    private static int rang(int xp) {
        if (xp < 500)
            return 0;
        else if (xp < 1500)
            return 1;
        else if (xp < 3500)
            return 2;
        else if (xp < 6000)
            return 3;
        else if (xp < 9000)
            return 4;
        else if (xp < 12000)
            return 5;
        else return 6;
    }

    public static String getDays(ProfileModel profileModel) {
        if (profileModel.getRegistration() != null) {
            Date now = new Date(), past = new Date(profileModel.getRegistration());
            int days = daysBetween(past, now);

            return days + " " + getDayAddition(days);
        } else return "null";
    }

    public static String getDays(Long date) {
        Date now = new Date(), past = new Date(date);
        int days = daysBetween(past, now);

        return days + " " + getDayAddition(days);
    }

    private static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private static String getDayAddition(int num) {
        int preLastDigit = num % 100 / 10;
        if (preLastDigit == 1) {
            return "дней";
        }

        switch (num % 10) {
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
