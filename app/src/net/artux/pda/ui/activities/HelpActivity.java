package net.artux.pda.ui.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.artux.pda.R;
import net.artux.pda.ui.main.SectionsPagerAdapter;
import net.artux.pda.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {

    private ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.yellow));
        tabs.setTabRippleColor(colorStateList);
        tabs.setTabTextColors(colorStateList);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellow));

        tabs.setupWithViewPager(viewPager);
    }
}