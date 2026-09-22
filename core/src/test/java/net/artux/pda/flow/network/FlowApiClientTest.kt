package net.artux.pda.flow.network

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Round-trips FlowApiClient against the real dev backend (same server
 * maestro/scripts/run_register_to_prologue.sh targets) with a disposable, timestamp-unique
 * test account, cleaned up in @After regardless of outcome - mirrors that script's own
 * create-then-delete pattern. FlowApiClient has no Gdx/Android/RoboVM dependency, so this
 * runs on a plain JVM; it's the one part of this flow that's actually exercisable without
 * Xcode/a device (see the plan's Risks section).
 *
 * Requires network access to dev.artux.net; not run as part of the default unit test suite
 * gate if that's ever wired into CI without network access.
 */
class FlowApiClientTest {

    private val api = FlowApiClient()
    private val timestamp = System.currentTimeMillis()
    private val email = "flow-test-$timestamp@artux.net"
    private val password = "FlowTest$timestamp!"
    // The backend rejects digits/symbols in nicknames specifically (confirmed via a real
    // 200-with-success:false response - see FlowApiClient.register()'s comment), unlike email/
    // password which allow them - letters only here.
    private val nickname = "FlowTestUser"
    private var registered = false

    @Before
    fun register() = runBlocking {
        val result = api.register(nickname, email, password)
        assertTrue("register() failed: ${result.exceptionOrNull()}", result.isSuccess)
        registered = true
    }

    @After
    fun cleanup() = runBlocking {
        if (registered) {
            api.deleteAccount(email, password)
        }
    }

    @Test
    fun `checkLogin succeeds with the credentials just registered`() = runBlocking {
        val result = api.checkLogin(email, password)
        assertTrue("checkLogin() failed: ${result.exceptionOrNull()}", result.isSuccess)
    }

    @Test
    fun `checkLogin fails with the wrong password`() = runBlocking {
        val result = api.checkLogin(email, "definitely-wrong-password")
        assertTrue("checkLogin() should have failed with a bad password", result.isFailure)
    }

    @Test
    fun `getStories parses the story list`() = runBlocking {
        val result = api.getStories(email, password)
        assertTrue("getStories() failed: ${result.exceptionOrNull()}", result.isSuccess)
    }
}
