package net.artux.pda.ui.activities;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.ui.viewmodels.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LogActivity extends AppCompatActivity {

    protected SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TextView textView = findViewById(R.id.logText);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        String text = settingsViewModel.getLogInString().replaceAll("\n", "<br>");
        textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
    }
}