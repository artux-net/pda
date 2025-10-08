package net.artux.pda.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.appodeal.ads.Appodeal.INTERSTITIAL
import com.appodeal.ads.Appodeal.REWARDED_VIDEO
import com.appodeal.ads.Appodeal.initialize
import com.appodeal.ads.Appodeal.setLogLevel
import com.appodeal.ads.Appodeal.setUserId
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.databinding.ActivityMainBinding
import net.artux.pda.model.StatusModel
import net.artux.pda.model.user.UserModel
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.activities.hierarhy.MainContract
import net.artux.pda.ui.activities.hierarhy.MainPresenter
import net.artux.pda.ui.fragments.chat.DialogsFragment
import net.artux.pda.ui.fragments.news.NewsFragment
import net.artux.pda.ui.fragments.notes.NoteFragment
import net.artux.pda.ui.fragments.profile.UserProfileFragment
import net.artux.pda.ui.fragments.stories.StoriesFragment
import net.artux.pda.ui.viewmodels.UserViewModel
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.utils.AndroidHelper
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity(), MainContract.View, View.OnClickListener {
    private var binding: ActivityMainBinding? = null
    var presenter: MainPresenter? = null
        private set
    private var timeChangeReceiver: BroadcastReceiver? = null
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy")
        .withZone(ZoneId.systemDefault())
    private var viewModel: UserViewModel? = null

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel == null) viewModel = ViewModelProvider(this).get(
            UserViewModel::class.java
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initialize(
            this,
            "dfc30cc869b9e15edfe0b7d0cbb8cf37b5291c8e9b4fbe4d",
            INTERSTITIAL or REWARDED_VIDEO,
            ApdInitializationCallback { list: List<ApdInitializationError?>? ->
                Timber.i("Appodeal initialization done")
                setLogLevel(Log.LogLevel.debug)
                if (!list.isNullOrEmpty())
                    for (err in list)
                    Timber.tag("Add Error").e(err)
            })
        presenter = MainPresenter()
        presenter!!.attachView(this)

        val intent = intent
        if (intent != null) {
            val destination = intent.getIntExtra("section", ScreenDestination.NEWS)
            when (destination) {
                ScreenDestination.STORIES -> presenter!!.addFragment(StoriesFragment(), false)
                ScreenDestination.PROFILE -> presenter!!.addFragment(UserProfileFragment(), false)
                else -> presenter!!.addFragment(NewsFragment(), false)
            }
            if (intent.hasExtra("status")) {
                val statusModel = intent.getSerializableExtra("status") as StatusModel?
                Snackbar.make(binding!!.root, statusModel!!.description, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") { view: View? -> }
                    .show()
                if (!statusModel.success) Timber.e(statusModel.description)
            }
        }

        viewModel!!.member.observe(this) { memberResult: UserModel ->
            presenter!!.setAdditionalTitle("PDA #" + memberResult.pdaId)
            setUserId(memberResult.email)
            firebaseAnalytics.setUserId(memberResult.id.toString())
            firebaseAnalytics.setUserProperty("email", memberResult.email)
            firebaseAnalytics.setUserProperty("name", memberResult.name)
            firebaseAnalytics.setUserProperty("pda_id", memberResult.pdaId.toString())
        }

        setListeners()
        Timber.i("Main activity created.")
        AndroidHelper.hideNavBar(window)
    }

    override fun setTitle(title: String) {
        binding!!.titleView.text = title
    }

    override fun setAdditionalTitle(title: String) {
        binding!!.rightTitleView.text = title
    }

    fun setListeners() {
        binding!!.newsButton.setOnClickListener(this)
        binding!!.messagesButton.setOnClickListener(this)
        binding!!.profileButton.setOnClickListener(this)
        binding!!.notesButton.setOnClickListener(this)
        binding!!.mapButton.setOnClickListener(this)
    }

    override fun setFragment(fragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
        if (addToBackStack) fragmentTransaction.addToBackStack(null)
        fragmentTransaction
            .replace(R.id.containerView, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        Timber.d("Set fragment: %s", fragment.javaClass.simpleName)
    }

    override fun setAdditionalFragment(fragment: AdditionalBaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.rightContainer, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
        Timber.d("Set second fragment: %s", fragment.javaClass.simpleName)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = visibleFragment as BaseFragment?
        if (fragment != null) {
            presenter!!.backPressed(fragment)
        }
    }

    val visibleFragment: Fragment?
        get() {
            val fragmentManager = supportFragmentManager
            val fragments = fragmentManager.fragments
            for (fragment in fragments) {
                if (fragment != null && fragment.isVisible && fragment is BaseFragment) return fragment
            }
            return null
        }

    public override fun onStart() {
        super.onStart()
        binding!!.timeView.text = timeFormatter.format(Instant.now())
        timeChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null && intent.action!!
                        .compareTo(Intent.ACTION_TIME_TICK) == 0
                ) binding!!.timeView.text = timeFormatter.format(
                    Instant.now()
                )
            }
        }

        registerReceiver(timeChangeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        setLoadingState(false)
    }

    public override fun onStop() {
        super.onStop()
        if (timeChangeReceiver != null) unregisterReceiver(timeChangeReceiver)
    }

    override fun setLoadingState(loadingState: Boolean) {
        runOnUiThread {
            Glide.with(applicationContext)
                .asGif()
                .load(Uri.parse("file:///android_asset/loadCube.gif"))
                .listener(object : RequestListener<GifDrawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<GifDrawable?>,
                        isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any,
                        target: Target<GifDrawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource != null) {
                            if (loadingState) resource.setLoopCount(1)
                        }
                        return false
                    }
                })
                .into((findViewById<View>(R.id.loadingCube) as ImageView))
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.newsButton) {
            presenter!!.addFragment(NewsFragment(), true)
        } else if (id == R.id.messagesButton) {
            presenter!!.addFragment(DialogsFragment(), true)
        } else if (id == R.id.profileButton) {
            presenter!!.addFragment(UserProfileFragment(), true)
        } else if (id == R.id.notesButton) {
            presenter!!.addFragment(NoteFragment(), true)
        } else if (id == R.id.mapButton) {
            presenter!!.addFragment(StoriesFragment(), true)
        }
    }
}

