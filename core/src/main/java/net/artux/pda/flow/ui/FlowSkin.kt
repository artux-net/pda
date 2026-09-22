package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import net.artux.pda.map.view.root.FontManager

/**
 * Loads the same skin the in-game HUD uses (assets/skins/cloud/cloud-form-ui.{json,atlas,png})
 * synchronously, outside the AssetManager/CoreComponent Dagger graph - that graph (see
 * AssetsFinder, GameStageModule) only exists once a map has actually loaded, but this flow's
 * screens run before that (registration/login/story selection/dialogue). Manually replicates
 * what SkinLoader does under the hood: add the atlas regions, inject the two BitmapFonts the
 * skin json references by name ("font"/"title"), then load the style definitions.
 */
object FlowSkin {

    fun load(): Skin {
        val fontManager = FontManager()
        val skin = Skin()
        skin.addRegions(TextureAtlas(Gdx.files.internal("skins/cloud/cloud-form-ui.atlas")))
        skin.add("font", fontManager.getDisposableFont(FontManager.LIBERAL_FONT, 24), BitmapFont::class.java)
        skin.add("title", fontManager.getDisposableFont(FontManager.IMPERIAL_FONT, 28), BitmapFont::class.java)
        skin.load(Gdx.files.internal("skins/cloud/cloud-form-ui.json"))
        return skin
    }
}
