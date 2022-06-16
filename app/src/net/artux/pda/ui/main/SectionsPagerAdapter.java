package net.artux.pda.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import net.artux.pda.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.help_tab1_title, R.string.help_tab2_title, R.string.help_tab3_title, R.string.help_tab4_title};
    private static final int[] TAB_CONTENT = new int[]{R.string.help_tab1_content, R.string.help_tab2_content, R.string.help_tab3_content};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 3) {
            return PlaceholderFragment.newInstance(mContext.getResources().getString(TAB_CONTENT[position]));
        }else {
            return TabFinishFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}