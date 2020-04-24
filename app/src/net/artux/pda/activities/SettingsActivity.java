package net.artux.pda.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.app.App;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setOnClickListener();

    }

    void setOnClickListener(){
        findViewById(R.id.clearImages).setOnClickListener(this);
        findViewById(R.id.signOut).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signOut:
                App.getDataManager().removeAllData();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.clearImages:
                Glide.get(this).clearDiskCache();
                break;
        }
    }
}
