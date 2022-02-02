package net.artux.pda.ui.activities;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.repositories.Result;
import net.artux.pda.viewmodels.MemberViewModel;

public class LoadingActivity extends AppCompatActivity {


    private boolean loaded, gifEnd, afterClearCache = false;
    private MemberViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(MemberViewModel.class);

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


        viewModel.getMember().observe(this, memberResult -> {
            if(memberResult instanceof Result.Success){
                if (afterClearCache){
                    loaded = true;
                    start();
                }else {
                    afterClearCache = true;
                    viewModel.updateMemberWithReset();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Member error, try to login again", Toast.LENGTH_SHORT).show();
                App.getDataManager().removeAllData();
                startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
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
