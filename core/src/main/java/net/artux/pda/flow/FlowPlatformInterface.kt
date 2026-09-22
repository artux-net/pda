package net.artux.pda.flow

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.artux.pda.flow.network.FlowApiClient
import net.artux.pda.map.utils.PlatformInterface

/**
 * Real-backend PlatformInterface for the map stage of this flow (see StageScreen's hand-off
 * to GdxAdapter) - the ios module's own MockPlatformInterface ignores send()/applyActions()
 * entirely since it has no backend to talk to; this one actually posts them, the same way
 * CommandController.syncNow()/process() does on Android (PUT api/v1/quest/commands).
 *
 * Fire-and-forget on its own CoroutineScope, matching PlatformInterface's synchronous, no-
 * result contract (the Android implementation does the same via its own background scope).
 */
class FlowPlatformInterface(
    private val api: FlowApiClient,
    private val email: String,
    private val password: String
) : PlatformInterface {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun putObjectToLuaContext(key: String, value: Any) {
        // No Lua scripting bridge in this flow yet - out of scope, see plan.
    }

    override fun send(data: Map<String, String>) {
        // Map-side "send" calls carry loose key/value pairs (see the map's own usage), not
        // the stage-action shape applyCommands expects; there's no dedicated endpoint for this
        // on the backend today, so this is a deliberate no-op for now rather than a guess.
        Gdx.app.log("FlowPlatformInterface", "send() not wired to a backend endpoint yet: $data")
    }

    override fun applyActions(actions: Map<String, List<String>>) {
        if (actions.isEmpty()) return
        scope.launch {
            api.applyCommands(actions, email, password)
                .onFailure { Gdx.app.error("FlowPlatformInterface", "applyActions sync failed", it) }
        }
    }

    override fun restart() {
        Gdx.app.exit()
    }

    override fun exit() {
        Gdx.app.exit()
    }

    override fun openLogs() {
        // No log viewer in this build.
    }
}
