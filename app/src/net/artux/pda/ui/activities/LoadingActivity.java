package net.artux.pda.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pdalib.Member;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoadingActivity extends AppCompatActivity {

    String TAG = "LoadingActivity";

    boolean loaded, gifEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Glide.with(this)
                .asGif()
                .load(Uri.parse("file:///android_asset/load.gif"))
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                gifEnd = true;
                                start();
                            }
                        });
                        return false;
                    }

                })
                .into((ImageView) findViewById(R.id.imageGif));

        App.getRetrofitService().getPdaAPI()
                .loginUser().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(@NonNull Call<Member> call, @NonNull Response<Member> response) {
                Member member = response.body();
                if (response.code()==502) {
                    Toast.makeText(getApplicationContext(), R.string.unable_connect, Toast.LENGTH_SHORT).show();
                    LoadingActivity.this.finish();
                }
                else if (member!=null) {
                    Timber.d("Good token, set member.");

                    App.getDataManager().setMember(member);
                    loaded = true;
                    start();
                } else {
                    Toast.makeText(getApplicationContext(), "Member error, try to login again", Toast.LENGTH_SHORT).show();
                    App.getDataManager().removeAllData();
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                    LoadingActivity.this.finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Member> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.error_server_connection, Toast.LENGTH_SHORT).show();
                LoadingActivity.this.finish();
            }
        });
    }

    void start(){
        if (loaded && (gifEnd || BuildConfig.DEBUG)){
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            LoadingActivity.this.finish();
        }
    }
}
