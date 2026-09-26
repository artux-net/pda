package net.artux.pda.flow

import com.badlogic.gdx.Gdx
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel

/**
 * Plain mutable state holder carried between screens of the pre-game flow (registration ->
 * login -> story selection -> stage dialogue -> map). No DI here on purpose: CoreComponent's
 * Dagger graph (used once the real map loads) requires a DataRepository/GameMap that only
 * exist after this flow has actually chosen a story and reached a map stage, so this flow runs
 * entirely outside that graph and hands off to it only at the very end (see StageScreen).
 */
class FlowSession {
    var email: String? = null
    var password: String? = null

    var story: StoryModel? = null
    var storyData: StoryDataModel? = null

    var currentChapterId: Long = 0
    var currentStageId: Long = 0

    // Static catalog, same for every map/story - fetched once (see StageScreen.loadItemsContainer)
    // rather than on every map load.
    var itemsContainer: ItemsContainerModel? = null

    fun credentialsOrThrow(): Pair<String, String> {
        val e = email ?: error("Not logged in: email missing")
        val p = password ?: error("Not logged in: password missing")
        return e to p
    }

    /**
     * Persists [email]/[password] so the next launch can skip straight past Registration/Login
     * (see PdaFlowGame.create/AutoLoginScreen) - same plaintext-in-local-prefs approach as the
     * Android app's own DataManager (SharedPreferences "login"/"pass"), not a new weakness.
     */
    fun save() {
        val e = email ?: return
        val p = password ?: return
        prefs().putString(KEY_EMAIL, e).putString(KEY_PASSWORD, p).flush()
    }

    fun clearSaved() {
        val p = prefs()
        p.remove(KEY_EMAIL)
        p.remove(KEY_PASSWORD)
        p.flush()
    }

    companion object {
        private const val PREFS_NAME = "flow_session"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"

        private fun prefs() = Gdx.app.getPreferences(PREFS_NAME)

        /** The last saved credentials, if any - checked once at startup by PdaFlowGame.create. */
        fun savedCredentials(): Pair<String, String>? {
            val p = prefs()
            val email = p.getString(KEY_EMAIL, "")
            val password = p.getString(KEY_PASSWORD, "")
            return if (email.isNotEmpty() && password.isNotEmpty()) email to password else null
        }
    }
}
