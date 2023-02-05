package net.artux.pda.ui.fragments.profile.helpers;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.ProfileModel;
import net.artux.pda.utils.URLHelper;

import java.time.Instant;

public class ProfileHelper {

    public static String getGroup(Gang gang, Context context) {
        return context.getResources().getStringArray(R.array.groups)[gang.getId()];
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
        if (avatar == null)
            Glide.with(context)
                    .asDrawable()
                    .load(AppCompatResources.getDrawable(context, R.mipmap.ic_launcher))
                    .into(imageView);
        else if (isInteger(avatar))
            Glide.with(context)
                    .load(Uri.parse("file:///android_asset/avatars/a" + (Integer.parseInt(avatar) + 1) + ".png"))
                    .into(imageView);
        else {
            avatar = URLHelper.getResourceURL(avatar);

            Glide.with(context)
                    .asDrawable()
                    .load(avatar)
                    .into(imageView);
        }
    }

    public static String getRangTitleByXp(int xp, Context context) {
        //ProfileModel.Rang rang = ProfileModel.getRang(xp); TODO
        return context.getResources().getStringArray(R.array.rang)[0];
    }

    public static String getRangTitleById(int id, Context context) {
        return context.getResources().getStringArray(R.array.rang)[id];
    }

    public static String getDays(ProfileModel profileModel) {
        if (profileModel.getRegistration() != null) {
            return getDays(profileModel.getRegistration());
        } else return "null";
    }

    public static String getDays(Instant date) {
        int days = (int) ((Instant.now().toEpochMilli() - date.toEpochMilli())
                / (1000 * 60 * 60 * 24));

        return days + " " + getDayAddition(days);
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
