package net.artux.pda.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.artux.pda.R;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TextView textView = findViewById(R.id.logText);
        textView.setText(getIntent().getStringExtra("text"));
    }
}