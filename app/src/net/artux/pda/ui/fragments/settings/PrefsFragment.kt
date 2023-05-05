package net.artux.pda.ui.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.ui.activities.LoginActivity
import net.artux.pda.ui.activities.MainActivity
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation
import net.artux.pda.ui.viewmodels.QuestViewModel
import net.artux.pda.ui.viewmodels.UserViewModel
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class PrefsFragment : PreferenceFragmentCompat() {

    private val questViewModel: QuestViewModel by viewModels()
    private val viewModel: UserViewModel by viewModels()
    private lateinit var navigationPresenter: FragmentNavigation.Presenter

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationPresenter = (requireActivity() as MainActivity).presenter
        Timber.d("Fragment %s created", javaClass.name)
        navigationPresenter.setTitle(getString(R.string.settings))

        val signOutPreference = findPreference<Preference>("sign_out")
        signOutPreference?.setOnPreferenceClickListener {
            viewModel.signOut()
            questViewModel.clear()
            clearSharedPreferences(requireContext().applicationContext)
            startActivity(Intent(activity, LoginActivity::class.java))
            requireActivity().finish()
            true
        }

        val clearAllCachePreference = findPreference<Preference>("clear_all_cache")
        clearAllCachePreference?.setOnPreferenceClickListener {
            questViewModel.clear()
            Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show()
            true
        }

        val clearImagesCachePreference = findPreference<Preference>("clear_images_cache")
        clearImagesCachePreference?.setOnPreferenceClickListener {
            Thread {
                Glide.get(requireContext()).clearDiskCache()
                Looper.prepare()
                Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show()
            }.start()
            true
        }


        val exitStoryPreference = findPreference<Preference>("exit_story")
        exitStoryPreference?.setOnPreferenceClickListener {
            questViewModel.exitStory()
            true
        }

        val resetDataPreference = findPreference<Preference>("reset_data")
        resetDataPreference?.setOnPreferenceClickListener {
            clearSharedPreferences(requireContext().applicationContext)
            questViewModel.resetData()
            true
        }

        questViewModel.storyData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show()
        }

    }


    private fun clearSharedPreferences(ctx: Context) {
        val dir = File(ctx.filesDir.parent!! + "/shared_prefs/")
        val children = dir.list()
        for (child in children!!) {
            if (child != "prefs.xml") {
                ctx.getSharedPreferences(child.replace(".xml", ""), Context.MODE_PRIVATE).edit()
                    .clear().apply()
                //delete the file
                File(dir, child).delete()
            }
        }
    }
}