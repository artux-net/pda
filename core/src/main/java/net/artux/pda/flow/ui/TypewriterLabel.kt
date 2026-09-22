package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * Scene2D equivalent of the Android app's TypeWriterTextView (app/src/.../ui/views/
 * TypeWriterTextView.java): reveals [fullText] a few characters at a time instead of all at
 * once. That one drives the reveal off a Handler.postDelayed loop (Android-only); this one
 * hooks into Label's own act(delta) callback, which Stage.act() already calls every frame -
 * no extra timer/thread needed.
 */
class TypewriterLabel(skin: Skin, styleName: String = "default") : Label("", skin, styleName) {

    private var fullText: String = ""
    private var revealedChars = 0
    private var elapsed = 0f
    var charsPerSecond = 40f

    fun setFullText(text: String) {
        fullText = text
        revealedChars = 0
        elapsed = 0f
        setText("")
    }

    fun skipToEnd() {
        revealedChars = fullText.length
        setText(fullText)
    }

    val isRevealing: Boolean get() = revealedChars < fullText.length

    override fun act(delta: Float) {
        super.act(delta)
        if (isRevealing) {
            elapsed += delta
            val target = (elapsed * charsPerSecond).toInt().coerceAtMost(fullText.length)
            if (target != revealedChars) {
                revealedChars = target
                setText(fullText.substring(0, revealedChars))
            }
        }
    }
}
