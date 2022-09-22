package net.artux.pda.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.ui.viewmodels.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class LoadingActivity extends AppCompatActivity {

    private boolean loaded, gifEnd;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        Glide.with(this)
                .asGif()
                .load(Uri.parse("file:///android_asset/ui/load.gif"))
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


        viewModel.getMember().observe(this, memberResult -> {
            loaded = true;
            Timber.i("User information loaded, try to start...");
            start();
        });
        viewModel.getStatus().observe(this, statusModel -> {
            if (!statusModel.isSuccess()) {
                Toast.makeText(getApplicationContext(), statusModel.getDescription(), Toast.LENGTH_LONG).show();
                viewModel.signOut();
                startActivity(new Intent(this, LoginActivity.class));
            }else Toast.makeText(getApplicationContext(), statusModel.getDescription(), Toast.LENGTH_LONG).show();
        });
        viewModel.updateMember();
    }

    public static boolean TESTING_MAP = false;

    void start() {
        if (loaded && (gifEnd || BuildConfig.DEBUG)) {
            if (TESTING_MAP) {
                Timber.i("Going to QuestActivity");
                startActivity(new Intent(this, QuestActivity.class));
            } else {
                Timber.i("Going to MainActivity");
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            }
            LoadingActivity.this.finish();
        }
    }
}
