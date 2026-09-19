package net.artux.pda.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.datatransport.Event
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.gdx.CoreFragment
import net.artux.pda.model.StatusModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.model.quest.NotificationModel
import net.artux.pda.model.quest.StageModel
import net.artux.pda.repositories.QuestSoundManager
import net.artux.pda.ui.fragments.quest.SellerFragment
import net.artux.pda.ui.fragments.quest.StageRootFragment
import net.artux.pda.ui.viewmodels.CommandViewModel
import net.artux.pda.ui.viewmodels.QuestViewModel
import net.artux.pda.ui.viewmodels.SellerViewModel
import net.artux.pda.ui.viewmodels.UserViewModel
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.utils.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class QuestActivity : FragmentActivity(), AndroidFragmentApplication.Callbacks {
    
    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var soundManager: QuestSoundManager

    private val questViewModel: QuestViewModel by viewModels()
    private val commandViewModel: CommandViewModel by viewModels()

    private lateinit var switcher: ImageSwitcher
    private lateinit var coreFragment: CoreFragment
    private lateinit var stageRootFragment: StageRootFragment
    private var currentBackground = ""
    private var currentFragment: Fragment? = null
    private var notificationDialog: AlertDialog? = null

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest)
        stageRootFragment = StageRootFragment()
        coreFragment = CoreFragment()
        val provider = ViewModelProvider(this)
        questViewModel.chapter.observe(this) { chapter: ChapterModel? ->
            if (chapter == null) return@observe

            //preload images
            for (stage in chapter.stages.values) {
                if (stage.background != null && !stage.background.isNullOrEmpty()) {
                    val backgroundUrl = URLHelper.getResourceURL(stage.background)
                    Glide.with(this@QuestActivity)
                        .downloadOnly()
                        .load(backgroundUrl)
                        .submit()
                }
            }
        }
        questViewModel.stage.observe(this) { stageModel: StageModel -> setStage(stageModel) }
        questViewModel.map.observe(this) { map: GameMap -> startMap(provider, map) }
        questViewModel.loadingState.observe(this) { flag: Boolean ->
            findViewById<View>(R.id.loadingProgressBar).visibility =
                if (flag) View.VISIBLE else View.GONE
        }
        questViewModel.status.observe(this) { statusModel: StatusModel ->
            Toast
                .makeText(this, "Ошибка: " + statusModel.description, Toast.LENGTH_LONG)
                .show()
            if (!statusModel.success) Timber.e(statusModel.description)
        }
        questViewModel.notification.observe(this) { notificationModel: NotificationModel? ->
            if (notificationModel == null) return@observe
            val builder = AlertDialog.Builder(this, R.style.PDANotificationStyle)
            builder.setIcon(drawableFromAssets(this, notificationModel.type.iconId))
            builder.setTitle(notificationModel.title)
            builder.setMessage(notificationModel.message)
            val dialog = builder.create()
            notificationDialog = dialog
            dialog.setOnDismissListener { if (notificationDialog === dialog) notificationDialog = null }
            val window = dialog.window
            window?.setGravity(Gravity.START)
            dialog.show()
        }
        commandViewModel.sellerEvent.observe(this) { data: Event<Int> ->
            val sellerId = data.payload
            val sellerFragment = SellerFragment.newInstance(sellerId)
            val mFragmentTransaction = supportFragmentManager.beginTransaction()
            mFragmentTransaction
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.containerView, sellerFragment, "seller")
                .addToBackStack("seller")
                .commit()
            Timber.d("Start seller activity - %s", sellerId)
        }
        commandViewModel.exitEvent.observe(this) { (destination): ScreenDestination ->
            if (destination == ScreenDestination.NONE) return@observe
            val intent = Intent(this@QuestActivity, MainActivity::class.java)
            intent.putExtra("section", destination)
            startActivity(intent)
            finish()
        }
        questViewModel.background.observe(this) { nextBackground: String? ->
            setBackground(nextBackground)
        }
        switcher = findViewById(R.id.switcher)
        switcher.inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        switcher.outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        AndroidHelper.hideNavBar(window)
        Timber.i("QuestActivity created")
        startLoading()
    }

    private val isMapActive: Boolean
        get() = currentFragment === coreFragment

    private fun setStage(stageModel: StageModel) {
        soundManager.resume()
        val mFragmentTransaction = supportFragmentManager.beginTransaction()
        if (coreFragment.isAdded) {
            mFragmentTransaction.hide(coreFragment)
            mFragmentTransaction.setMaxLifecycle(coreFragment, Lifecycle.State.STARTED)
        }
        if (!stageRootFragment.isAdded) mFragmentTransaction.add(
            R.id.containerView,
            stageRootFragment,
            "root"
        )
        mFragmentTransaction
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .show(stageRootFragment)
            .commitNow()
        stageRootFragment.setStage(stageModel)
        currentFragment = stageRootFragment
    }

    private fun startMap(provider: ViewModelProvider, map: GameMap) {
        soundManager.pause()
        val mFragmentTransaction = supportFragmentManager.beginTransaction()
        val args = Bundle()
        args.putSerializable("map", map)
        args.putSerializable("data", questViewModel.storyData.value)
        args.putSerializable("story", questViewModel.getCurrentStory())
        args.putSerializable("user", provider[UserViewModel::class.java].getFromCache())
        args.putSerializable("items", provider[SellerViewModel::class.java].getItems())
        args.putBoolean("updated", true)
        coreFragment.arguments = args
        if (coreFragment.isAdded) {
            if (coreFragment.isHidden) {
                mFragmentTransaction.setMaxLifecycle(coreFragment, Lifecycle.State.RESUMED)
                mFragmentTransaction.show(coreFragment)
            } else {
                coreFragment.onResume()
            }
        }
        mFragmentTransaction
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.containerView, coreFragment)
            .commitNow()
        currentFragment = coreFragment
    }

    private fun startLoading() {
        val keys = intent.getIntArrayExtra("keys")
        // keys for loading specific stage
        if (keys == null) {
            val storyId = intent.getIntExtra("storyId", -1)
            if (storyId < 0) {
                // если нет номера истории в намерении
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("section", ScreenDestination.STORIES)
                startActivity(intent)
                finish()
                Timber.i("Story ID not specified, closing QuestActivity")
                return
            }
            val sync = !intent.getBooleanExtra("current", false)
            val chapter = intent.getIntExtra("chapterId", 1)
            val stage = intent.getIntExtra("stageId", 0)

            // загрузка последней стадии или намеренной
            questViewModel.beginWithStage(storyId, chapter, stage.toLong(), sync)
            firebaseAnalytics.logEvent("stage_begin"){
                param("story_id", storyId.toString())
                param("chapter_id", chapter.toString())
                param("stage_id", stage.toString())
            }
            Timber.i("Story started from args %d, %d, %d", storyId, chapter, stage)
        } else {
            questViewModel.beginWithStage(keys[0], keys[1], keys[2].toLong(), true)
            Timber.i("Story started from keys %d, %d, %d", keys[0], keys[1], keys[2])
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * Gamepad/keyboard support for the quest screen only - the map (CoreFragment) has its own
     * GamepadInputSystem/KeyboardInputSystem reading input directly, so events are left untouched
     * (falling through to super) whenever the map is on screen.
     *
     * Android already handles arrow-key/D-pad focus movement and ENTER/DPAD_CENTER clicking the
     * focused view for free (standard View.onKeyUp() behavior), which is enough for a hardware
     * keyboard to navigate quest choices. A gamepad's face buttons aren't treated as a "click"
     * the same way, though, so KEYCODE_BUTTON_A needs to be translated into a click on whatever
     * choice button D-pad/stick navigation last focused (see StageFragment).
     *
     * KEYCODE_BUTTON_B and KEYCODE_ESCAPE call the dismiss/back action directly instead of
     * synthesizing a KEYCODE_BACK KeyEvent and redispatching it - on API levels with predictive
     * back enabled, a synthetic BACK event doesn't reliably reach onBackPressed() (even the real
     * hardware/gesture back button ends up closing the whole task instead here), so this calls
     * exactly what should happen instead of hoping the system routes it there.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (!isMapActive && event.action == KeyEvent.ACTION_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BUTTON_A -> {
                    currentFocus?.performClick()
                    return true
                }
                KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_ESCAPE -> {
                    val dialog = notificationDialog
                    if (dialog != null && dialog.isShowing) dialog.dismiss() else onBackPressed()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun setBackground(nextBackground: String?) {
        if (nextBackground == null) return
        if (currentBackground != nextBackground) {
            currentBackground = URLHelper.getResourceURL(nextBackground)
            Glide.with(this)
                .load(currentBackground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into((switcher.nextView as ImageView))
            switcher.showNext()
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.pause()
    }

    override fun onResume() {
        super.onResume()
        soundManager.resume()
    }

    override fun onDestroy() {
        Timber.i("QuestActivity destroyed")
        soundManager.stop()
        super.onDestroy()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    override fun exit() {}
}