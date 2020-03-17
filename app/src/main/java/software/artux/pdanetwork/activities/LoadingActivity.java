package software.artux.pdanetwork.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import software.artux.pdanetwork.Models.Member;
import com.devilsoftware.pdanetwork.R;
import software.artux.pdanetwork.app.App;

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
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member!=null) {
                    App.getDataManager().setMember(member);
                    startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                    LoadingActivity.this.finish();
                }else{
                    Toast.makeText(LoadingActivity.this, "Member error, try to login again", Toast.LENGTH_SHORT).show();
                    App.getDataManager().removeAllData();
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                    LoadingActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(LoadingActivity.this, R.string.unable_connect, Toast.LENGTH_SHORT).show();
                LoadingActivity.this.finish();
            }
        });
    }


}
