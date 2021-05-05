package net.artux.pda.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pdalib.Member;
import net.artux.pdalib.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setOnClickListener();
        TextView debug = findViewById(R.id.debugMember);
        TextView version = findViewById(R.id.version);
        version.setText(getResources().getString(R.string.version, BuildConfig.VERSION_NAME));
        TextView build = findViewById(R.id.build);
        build.setText(getResources().getString(R.string.build, String.valueOf(BuildConfig.VERSION_CODE)));


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(App.getDataManager().getMember());
        debug.setText(json);
    }

    void setOnClickListener(){
        findViewById(R.id.clearImages).setOnClickListener(this);
        findViewById(R.id.signOut).setOnClickListener(this);
        findViewById(R.id.showDebug).setOnClickListener(this);
        findViewById(R.id.resetData).setOnClickListener(this);
        findViewById(R.id.resetStory).setOnClickListener(this);
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
                new Thread(() -> Glide.get(SettingsActivity.this).clearDiskCache()).start();
                break;
            case R.id.showDebug:
                if (findViewById(R.id.debugMember).getVisibility()==View.VISIBLE)
                    findViewById(R.id.debugMember).setVisibility(View.GONE);
                else
                    findViewById(R.id.debugMember).setVisibility(View.VISIBLE);
                break;
            case R.id.resetData:
                App.getRetrofitService().getPdaAPI().resetData().enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        Status status = response.body();
                        if (status!=null){
                            Toast.makeText(getApplicationContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_server_connection), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_server_connection), Toast.LENGTH_LONG).show();
                        throwable.printStackTrace();
                    }
                });
                break;
            case R.id.resetStory:
                HashMap<String, List<String>> action = new HashMap<>();
                action.put("reset_current", new ArrayList<>());
                App.getRetrofitService().getPdaAPI().synchronize(action).enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        Member member = response.body();
                        if (member!=null) {
                            App.getDataManager().setMember(member);
                        }
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {
                        Timber.e(t);
                    }
                });
                break;
        }
    }
}
