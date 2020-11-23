package net.artux.pda.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pdalib.Member;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Glide.with(this)
                .load(Uri.parse("file:///android_asset/load.gif"))
                .into((ImageView) findViewById(R.id.imageGif));

        App.getRetrofitService().getPdaAPI()
                .loginUser().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(@NonNull Call<Member> call, @NonNull Response<Member> response) {
                Member member = response.body();
                if (response.code()==502)
                    Toast.makeText(LoadingActivity.this, R.string.unable_connect, Toast.LENGTH_SHORT).show();
                else if (member!=null) {
                    System.out.println("set member: " + new Gson().toJson(member));
                    App.getDataManager().setMember(member);
                    startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoadingActivity.this, "Member error, try to login again", Toast.LENGTH_SHORT).show();
                    App.getDataManager().removeAllData();
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                }
                LoadingActivity.this.finish();
            }

            @Override
            public void onFailure(@NonNull Call<Member> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoadingActivity.this, R.string.error_server_connection, Toast.LENGTH_SHORT).show();
                LoadingActivity.this.finish();
            }
        });
    }


}
