package net.artux.pda.ui.fragments.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.ui.activities.LogActivity
import net.artux.pda.ui.activities.LoginActivity
import net.artux.pda.ui.activities.MainActivity
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation
import net.artux.pda.ui.viewmodels.QuestViewModel
import net.artux.pda.ui.viewmodels.SettingsViewModel
import net.artux.pda.ui.viewmodels.UserViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets


@AndroidEntryPoint
class PrefsFragment : PreferenceFragmentCompat() {

    private val questViewModel: QuestViewModel by viewModels()
    private val viewModel: UserViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var navigationPresenter: FragmentNavigation.Presenter

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var baseDocumentTreeUri: Uri? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                baseDocumentTreeUri = it.data?.data
                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(
                    it.data?.data!!, takeFlags)

                writeFile("pda_log", settingsViewModel.getLogInString())
            } else {
                Timber.e("Получен неуспешный код: $it")
            }
        }
        navigationPresenter = (requireActivity() as MainActivity).presenter
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

        val showLogsPreference = findPreference<Preference>("show_logs")
        showLogsPreference?.setOnPreferenceClickListener {
            settingsViewModel.update()
            true
        }

        settingsViewModel.log.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), LogActivity::class.java)
            requireContext().startActivity(intent)
        }

        val saveLogsPreference = findPreference<Preference>("save_logs")
        saveLogsPreference?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            launcher.launch(intent)
            true
        }

        val resetLogsPreference = findPreference<Preference>("reset_logs")
        resetLogsPreference?.setOnPreferenceClickListener {
            settingsViewModel.resetLogFile()
            true
        }

        questViewModel.status.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.description, Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeFile(fileName: String, content: String) {
        try{
            var result =
                "<html><head><meta charset=\"UTF-8\"/></head><body>$content</body></html>"
            result = result.replace(System.lineSeparator(), "<br>")
            val directory = DocumentFile.fromTreeUri(requireContext(), baseDocumentTreeUri!!)!!
            val file = directory.createFile("text/html", fileName)
            val pfd = requireContext().contentResolver.openFileDescriptor(file!!.uri, "w")
            val fos = FileOutputStream(pfd!!.fileDescriptor)
            fos.write(result.toByteArray(StandardCharsets.UTF_8))
            fos.close()
        } catch (_: IOException) {}
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