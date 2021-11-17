package net.artux.pda.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.app.NotificationService;
import net.artux.pda.databinding.ActivityMainBinding;
import net.artux.pda.gdx.CoreStarter;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.activities.hierarhy.MainContract;
import net.artux.pda.ui.activities.hierarhy.MainPresenter;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.additional.InfoFragment;
import net.artux.pda.ui.fragments.chat.DialogsFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.news.NewsFragment;
import net.artux.pda.ui.fragments.notes.NoteFragment;
import net.artux.pda.ui.fragments.notes.NotesFragment;
import net.artux.pda.ui.fragments.profile.ProfileFragment;
import net.artux.pda.ui.fragments.stories.StoriesFragment;
import net.artux.pdalib.Member;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends FragmentActivity implements MainContract.View, View.OnClickListener {

    private ActivityMainBinding binding;

    public BaseFragment mainFragment;
    public AdditionalBaseFragment additionalFragment;
    public MainPresenter presenter;

    private BroadcastReceiver timeReceiver;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new MainPresenter();
        presenter.attachView(this);
        //App.getRetrofitService().attachView(this);

        Intent intent = getIntent();

        if (intent!=null && intent.getStringExtra("section")!=null) {
            switch (getIntent().getStringExtra("section")) {
                case "stories":
                    setFragment(new StoriesFragment(), false);
                    break;
                case "profile":
                    setFragment(new ProfileFragment(), false);
                    break;
                default:
                    setFragment(new NewsFragment(), false);
                    break;
            }
        }
        else{
            setFragment(new NewsFragment(), false);
        }
        setAdditionalFragment(new InfoFragment());


        if(App.getDataManager().getMember()!=null){
            setAdditionalTitle("PDA #" + App.getDataManager().getMember().getPdaId());
        }else
            startActivity(new Intent(this, LoadingActivity.class));

        setListeners();
        //startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void setTitle(String title){
        binding.titleView.setText(title);
    }

    @Override
    public void setAdditionalTitle(String title){
        binding.rightTitleView.setText(title);
    }

    @Override
    public void passData(Bundle data) {
        mainFragment.receiveData(data);
        additionalFragment.receiveData(data);
    }

    void setListeners(){
        binding.newsButton.setOnClickListener(this);
        binding.messagesButton.setOnClickListener(this);
        binding.profileButton.setOnClickListener(this);
        binding.notesButton.setOnClickListener(this);
        binding.mapButton.setOnClickListener(this);
    }

    @Override
    public void setFragment(BaseFragment fragment, boolean addToBackStack) {
        mainFragment = fragment;
        fragment.attachPresenter(presenter);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction
                .replace(R.id.containerView,fragment)
                .commit();
        Timber.d("Set fragment: %s", fragment.getClass().getSimpleName());
    }

    @Override
    public void setAdditionalFragment(AdditionalBaseFragment fragment) {
        additionalFragment = fragment;
        fragment.attachPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rightContainer,fragment)
                .commit();
        Timber.d("Set second fragment: %s", fragment.getClass().getSimpleName());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {

        if (App.getDataManager().getMember()==null)
            startActivity(new Intent(this, LoginActivity.class));
        super.onStart();
        binding.timeView.setText(timeFormat.format(new Date()));
        timeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction()!=null && intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    binding.timeView.setText(timeFormat.format(new Date()));
            }
        };

        registerReceiver(timeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        setLoadingState(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timeReceiver != null)
            unregisterReceiver(timeReceiver);
    }

    @Override
    public void setLoadingState(boolean loadingState){
        runOnUiThread(() -> Glide.with(getApplicationContext())
            .asGif()
            .load(Uri.parse("file:///android_asset/loadCube.gif"))
            .listener(new RequestListener<GifDrawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (resource != null) {
                        if(loadingState) resource.setLoopCount(1);
                    }
                    return false;
                }
            })
            .into((ImageView) findViewById(R.id.loadingCube)));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.newsButton) {
            setFragment(new NewsFragment(), true);
            setAdditionalFragment(new InfoFragment());
        } else if (id == R.id.messagesButton) {
            setFragment(new DialogsFragment(), true);
            setAdditionalFragment(new InfoFragment());
        } else if (id == R.id.profileButton) {
            updateMember();
            setFragment(new UserProfileFragment(), true);
            setAdditionalFragment(new AdditionalFragment());
        } else if (id == R.id.notesButton) {
            setFragment(new NoteFragment(), true);
            setAdditionalFragment(new NotesFragment());
        } else if (id == R.id.mapButton) {
            setFragment(new StoriesFragment(), true);
            setAdditionalFragment(new InfoFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMember();
    }

    private void updateMember(){
        App.getRetrofitService().getPdaAPI()
                .loginUser().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(@NonNull Call<Member> call, @NonNull Response<Member> response) {
                Member member = response.body();
                if (response.code()==502)
                    Toast.makeText(MainActivity.this, R.string.unable_connect, Toast.LENGTH_SHORT).show();
                else if (member!=null) {
                    Timber.d("Member updated");
                    App.getDataManager().setMember(member);
                } else {
                    Toast.makeText(MainActivity.this, "Member error, try to login again", Toast.LENGTH_SHORT).show();
                    App.getDataManager().removeAllData();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Member> call, Throwable t) {
                Timber.e(t);
                Toast.makeText(MainActivity.this, R.string.error_server_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

