package net.artux.pda.flow.ui

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Screen

/**
 * Bridges the existing map system (net.artux.pda.map.GdxAdapter, built via its own unchanged
 * Builder - see StageScreen) into this flow's Game/Screen world. GdxAdapter implements
 * ApplicationAdapter (create/render/resize/pause/resume/dispose), not Screen (which also has
 * show/hide) - this just forwards one to the other so Game.setScreen()/goTo() can host it like
 * any other screen in the flow.
 */
class GdxAdapterScreen(private val adapter: ApplicationAdapter) : Screen {

    private var created = false

    override fun show() {
        if (!created) {
            adapter.create()
            created = true
        }
        adapter.resume()
    }

    override fun render(delta: Float) = adapter.render()

    override fun resize(width: Int, height: Int) = adapter.resize(width, height)

    override fun pause() = adapter.pause()

    override fun resume() = adapter.resume()

    override fun hide() = adapter.pause()

    override fun dispose() = adapter.dispose()
}
