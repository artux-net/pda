package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import net.artux.pda.map.view.root.FontManager

/**
 * The pre-game flow's look, matching the Android app's login/registration/quest screens
 * (app/res/values/colors.xml, styles.xml' EditTextStyle, drawable/bottom_border.xml,
 * StageFragment's choice buttons). Built in code rather than from the in-game HUD's cloud skin,
 * whose white Windows-style widgets had nothing in common with the Android screens.
 *
 * Sizes are in points - ScreenViewport's units on iOS - which correspond to Android's dp/sp.
 *
 * Styles:
 *  - Label: "default", "title", "hint", "card-title"
 *  - TextButton: "default" (bottom_border: bold text over a thin yellow line),
 *    "choice" (StageFragment's black_overlay block, yellow outline while pressed)
 *  - Button: "choice" (same block, for multi-line story cards)
 *  - TextField: "default" (EditTextStyle: grey hint, yellow underline)
 *  - ScrollPane: "default" (bare, thin scrollbar), "panel" (black_overlay text panel)
 *  - drawable "banner": the login/registration logo
 */
object FlowSkin {

    val BACKGROUND: Color = Color.valueOf("161719")
    val YELLOW: Color = Color.valueOf("f2cd34")
    val OVERLAY: Color = Color.valueOf("000000af")
    val TEXT: Color = Color.valueOf("f3f3f3")
    val HINT: Color = Color.valueOf("808080")
    val DISABLED: Color = Color.valueOf("5a5a5a")

    // FontManager's set lacks «» — … №, which quest texts use; missing glyphs render as nothing.
    private const val CHARACTERS = FreeTypeFontGenerator.DEFAULT_CHARS +
        "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ«»—–…№“”„’"

    fun load(): Skin {
        val skin = Skin()

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(Color.WHITE); fill() }
        val whiteTexture = Texture(pixmap)
        pixmap.dispose()
        skin.add("white-texture", whiteTexture)
        val white = TextureRegion(whiteTexture)
        skin.add("white", white)

        // 1162x390, drawn near 1:1 on a Retina phone - no mipmaps (NPOT on GLES2 can't have them).
        val bannerTexture = Texture(Gdx.files.internal("flow/banner.png")).apply {
            setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        }
        skin.add("banner-texture", bannerTexture)
        skin.add("banner", TextureRegion(bannerTexture))

        val body = font(skin, "font", FontManager.LIBERAL_FONT, 17)
        val bold = font(skin, "font-bold", FontManager.LIBERAL_FONT, 17, bold = true)
        val cardTitle = font(skin, "font-card", FontManager.LIBERAL_FONT, 20)
        val title = font(skin, "title", FontManager.IMPERIAL_FONT, 28)

        skin.add("default", Label.LabelStyle(body, TEXT))
        skin.add("title", Label.LabelStyle(title, Color.WHITE))
        skin.add("hint", Label.LabelStyle(body, HINT))
        skin.add("card-title", Label.LabelStyle(cardTitle, Color.WHITE))

        // drawable/bottom_border.xml: a 1dp yellow line under bold text. Pressed turns the text
        // yellow too - Android's link-style TextViews had no other press feedback worth copying.
        skin.add("default", TextButton.TextButtonStyle().apply {
            font = bold
            fontColor = TEXT
            downFontColor = YELLOW
            overFontColor = YELLOW
            disabledFontColor = DISABLED
            up = Underline(white, YELLOW, 1f).pad(horizontal = 10f, vertical = 10f)
            disabled = Underline(white, DISABLED, 1f).pad(horizontal = 10f, vertical = 10f)
        })

        // StageFragment.choiceBackground(): black_overlay fill, 2dp yellow stroke when focused.
        val choiceUp = Box(white, OVERLAY, null, 0f).pad(horizontal = 12f, vertical = 10f)
        val choiceDown = Box(white, OVERLAY, YELLOW, 2f).pad(horizontal = 12f, vertical = 10f)
        skin.add("choice", TextButton.TextButtonStyle().apply {
            font = body
            fontColor = TEXT
            disabledFontColor = DISABLED
            up = choiceUp
            down = choiceDown
        })
        skin.add("choice", Button.ButtonStyle().apply {
            up = choiceUp
            down = choiceDown
        })

        // EditTextStyle: white text, #808080 hint, yellow underline (colorControlNormal/Activated).
        skin.add("default", TextField.TextFieldStyle().apply {
            font = body
            fontColor = TEXT
            messageFont = body
            messageFontColor = HINT
            disabledFontColor = DISABLED
            background = Underline(white, YELLOW, 1f).pad(horizontal = 4f, vertical = 10f)
            focusedBackground = Underline(white, YELLOW, 2f).pad(horizontal = 4f, vertical = 10f)
            cursor = Box(white, YELLOW, null, 0f).apply { minWidth = 2f }
            selection = Box(white, YELLOW.cpy().apply { a = 0.35f }, null, 0f)
        })

        val scrollKnob = Box(white, HINT.cpy().apply { a = 0.6f }, null, 0f).apply { minWidth = 3f; minHeight = 20f }
        skin.add("default", ScrollPane.ScrollPaneStyle().apply { vScrollKnob = scrollKnob })
        // fragment_quest0's sceneText: black_overlay behind the text, 10dp padding.
        skin.add("panel", ScrollPane.ScrollPaneStyle().apply {
            background = Box(white, OVERLAY, null, 0f).pad(horizontal = 12f, vertical = 10f)
            vScrollKnob = scrollKnob
        })

        return skin
    }

    /**
     * Rasterised at the real pixel size and scaled back down: ScreenViewport works in points, so
     * a font generated at its point size was stretched 3x on a Retina iPhone and looked blurry.
     */
    private fun font(skin: Skin, name: String, path: String, sizePt: Int, bold: Boolean = false): BitmapFont {
        val density = (Gdx.graphics.backBufferWidth.toFloat() / Gdx.graphics.width).coerceAtLeast(1f)
        val generator = FreeTypeFontGenerator(Gdx.files.internal(path))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = Math.round(sizePt * density)
            characters = CHARACTERS
            minFilter = Texture.TextureFilter.Linear
            magFilter = Texture.TextureFilter.Linear
            if (bold) {
                // No bold face is bundled; a same-coloured outline thickens the strokes, and
                // stays tintable since both are white in the atlas.
                borderWidth = 0.7f * density
                borderColor = Color.WHITE
            }
        }
        val font = generator.generateFont(parameter)
        generator.dispose()
        font.data.setScale(1f / density)
        font.setUseIntegerPositions(false)
        skin.add(name, font)
        return font
    }

    private fun <T : BaseDrawable> T.pad(horizontal: Float, vertical: Float): T = apply {
        leftWidth = horizontal
        rightWidth = horizontal
        topHeight = vertical
        bottomHeight = vertical
    }

    /** A line along the bottom edge only - Android's bottom_border / EditText underline. */
    private class Underline(
        private val region: TextureRegion,
        private val color: Color,
        private val thickness: Float
    ) : BaseDrawable() {
        override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
            val previous = batch.packedColor
            batch.setColor(color.r, color.g, color.b, color.a * batch.color.a)
            batch.draw(region, x, y, width, thickness)
            batch.packedColor = previous
        }
    }

    /** Solid fill with an optional outline - GradientDrawable's color + stroke. */
    private class Box(
        private val region: TextureRegion,
        private val fill: Color,
        private val stroke: Color?,
        private val strokeWidth: Float
    ) : BaseDrawable() {
        override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
            val previous = batch.packedColor
            val alpha = batch.color.a
            batch.setColor(fill.r, fill.g, fill.b, fill.a * alpha)
            batch.draw(region, x, y, width, height)
            if (stroke != null && strokeWidth > 0f) {
                batch.setColor(stroke.r, stroke.g, stroke.b, stroke.a * alpha)
                batch.draw(region, x, y, width, strokeWidth)
                batch.draw(region, x, y + height - strokeWidth, width, strokeWidth)
                batch.draw(region, x, y, strokeWidth, height)
                batch.draw(region, x + width - strokeWidth, y, strokeWidth, height)
            }
            batch.packedColor = previous
        }
    }
}
