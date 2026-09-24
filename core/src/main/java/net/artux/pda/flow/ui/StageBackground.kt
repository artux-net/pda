package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Scaling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.artux.pda.flow.network.FlowApiClient

/**
 * Full-screen stage background (Stage.background - a CDN path, or a full URL), the libGDX
 * counterpart of QuestActivity's Glide-loaded background. Download and JPEG/PNG decode happen
 * off the GL thread; only Texture creation runs on it. Like Android, a stage without a
 * background keeps the previous one.
 */
class StageBackground(private val api: FlowApiClient) : Disposable {

    val image = Image().apply {
        setFillParent(true)
        setScaling(Scaling.fill)
        touchable = Touchable.disabled
        // Dimmed so the dialogue text drawn straight on top stays readable.
        setColor(DIM, DIM, DIM, 0f)
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Access-ordered, so iteration starts from the least recently used - GL thread only.
    private val textures = LinkedHashMap<String, Texture>(16, 0.75f, true)
    private val inFlight = HashSet<String>()
    private var wantedUrl: String? = null
    private var disposed = false

    /** Shows [path]'s image, fading it in once loaded; null/blank keeps the current one. */
    fun show(path: String?) {
        if (path.isNullOrBlank()) return
        val url = FlowApiClient.resourceUrl(path)
        if (url == wantedUrl) return
        wantedUrl = url
        val cached = textures[url]
        if (cached != null) display(cached) else load(url)
    }

    /** Warms the cache for backgrounds the player may reach next. */
    fun prefetch(paths: Collection<String?>) {
        paths.filterNotNull().filter { it.isNotBlank() }
            .map { FlowApiClient.resourceUrl(it) }
            .filter { it !in textures }
            .forEach { load(it) }
    }

    private fun load(url: String) {
        if (!inFlight.add(url)) return
        scope.launch {
            val pixmap = api.download(url).mapCatching { bytes -> Pixmap(bytes, 0, bytes.size) }
            Gdx.app.postRunnable { onLoaded(url, pixmap) }
        }
    }

    private fun onLoaded(url: String, pixmap: Result<Pixmap>) {
        inFlight.remove(url)
        pixmap.onFailure { Gdx.app.error(TAG, "Can't load background $url", it) }
        val loaded = pixmap.getOrNull() ?: return
        if (disposed) {
            loaded.dispose()
            return
        }
        val texture = Texture(loaded)
        loaded.dispose()
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        textures[url] = texture
        evictOverflow()
        if (url == wantedUrl) display(texture)
    }

    private fun display(texture: Texture) {
        image.drawable = TextureRegionDrawable(texture)
        image.clearActions()
        image.color.a = 0f
        image.addAction(Actions.alpha(1f, FADE_SECONDS))
    }

    private fun evictOverflow() {
        val iterator = textures.entries.iterator()
        while (textures.size > MAX_TEXTURES && iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == wantedUrl) continue
            entry.value.dispose()
            iterator.remove()
        }
    }

    override fun dispose() {
        disposed = true
        scope.cancel()
        textures.values.forEach { it.dispose() }
        textures.clear()
    }

    private companion object {
        const val TAG = "StageBackground"
        const val DIM = 0.45f
        const val FADE_SECONDS = 0.3f

        // A full-HD background is ~6 MB of GPU memory - enough for the current stage plus
        // what its transfers lead to, without holding a whole chapter's worth.
        const val MAX_TEXTURES = 6
    }
}
