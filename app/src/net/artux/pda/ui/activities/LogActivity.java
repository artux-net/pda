package net.artux.pda.ui.activities;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.artux.pda.R;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TextView textView = findViewById(R.id.logText);
        String text = getIntent()
                .getStringExtra("text")
                .replaceAll("\n", "<br>");
        textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
    }
}