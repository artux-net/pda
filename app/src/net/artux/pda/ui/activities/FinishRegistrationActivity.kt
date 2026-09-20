package net.artux.pda.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.databinding.ActivityFinishRegistrationBinding
import net.artux.pda.model.quest.StoryItem
import net.artux.pda.model.user.LoginUser
import net.artux.pda.ui.viewmodels.AuthViewModel
import net.artux.pda.ui.viewmodels.StoriesViewModel

/**
 * Registration succeeded; tapping "OK, log in" here used to just finish() back to LoginActivity
 * for a manual login. Now it logs the user in with the credentials they just registered with and
 * drops them straight into the first story with no prerequisites (the prologue, for a fresh
 * account) instead - see AuthViewModel.login()/StoriesViewModel for the reused pieces.
 */
@AndroidEntryPoint
class FinishRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishRegistrationBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val storiesViewModel: StoriesViewModel by viewModels()

    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishRegistrationBinding.inflate(layoutInflater)

        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")

        binding.registrationEndDesc.text = getString(R.string.registrationEndDesc, email)
        binding.endBtn.setOnClickListener { startPrologue() }
        setContentView(binding.root)

        authViewModel.member.observe(this) { storiesViewModel.updateData() }
        authViewModel.status.observe(this) { status ->
            if (!status.success) {
                Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_LONG).show()
                goToLogin()
            }
        }
        storiesViewModel.storyData.observe(this) { storiesViewModel.updateStories() }
        storiesViewModel.stories.observe(this) { stories ->
            val prologue = stories.firstOrNull { !it.complete && it.needs.isEmpty() }
            if (prologue != null) storiesViewModel.selectStory(prologue) else goToLogin()
        }
        storiesViewModel.storySelector.observe(this) { selector ->
            val questIntent = Intent(this, QuestActivity::class.java)
            questIntent.putExtra("current", selector.isCurrent)
            questIntent.putExtra("storyId", selector.storyId)
            questIntent.putExtra("chapterId", selector.chapterId)
            questIntent.putExtra("stageId", selector.stageId)
            startActivity(questIntent)
            finish()
        }
    }

    private fun startPrologue() {
        val loginPassword = password
        if (loginPassword == null || email == null) {
            // Shouldn't happen - RegisterActivity always passes both - but a manual login is a
            // safe fallback rather than crashing on a null LoginUser field.
            goToLogin()
            return
        }
        showProgress(true)
        authViewModel.login(LoginUser(email!!, loginPassword))
    }

    private fun showProgress(show: Boolean) {
        binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
        binding.registerProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun goToLogin() {
        showProgress(false)
        finish()
    }
}
