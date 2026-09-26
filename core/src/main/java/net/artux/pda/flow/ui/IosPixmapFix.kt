package net.artux.pda.flow.ui

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap

/**
 * gdx-backend-robovm's native PNG/JPEG decoder swaps red and blue on iOS: banner.png's
 * dominant #FFCC01 yellow (confirmed by inspecting the file - no blue in it at all) renders
 * as blue on-device, while scene2d's vertex-tinted colors (yellow underlines/text, drawn by
 * FlowSkin, not decoded from an image) render correctly. Not reproducible on Android/desktop,
 * so every real decoded image on iOS needs its R and B channels swapped back after loading.
 */
object IosPixmapFix {

    fun apply(pixmap: Pixmap): Pixmap {
        if (Gdx.app.type != Application.ApplicationType.iOS) return pixmap
        val bytesPerPixel = when (pixmap.format) {
            Pixmap.Format.RGBA8888 -> 4
            Pixmap.Format.RGB888 -> 3
            else -> return pixmap
        }
        val buffer = pixmap.pixels
        var i = 0
        while (i < buffer.capacity()) {
            val r = buffer.get(i)
            buffer.put(i, buffer.get(i + 2))
            buffer.put(i + 2, r)
            i += bytesPerPixel
        }
        return pixmap
    }
}
