package net.artux.pda.flow.network.dto

import com.google.gson.Gson
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.user.Gang
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Offline check of the enum-as-name fields in GET api/v1/user/quest/info (see app/api.json's
 * StoryData schema). FlowToMapTest's fresh account owns no items, so it never exercises these.
 */
class StoryDataDtoTest {

    private val json = """
        {
          "gang": "DUTY",
          "parameters": [{"key": "met_barman", "value": 1}],
          "weapons": [{"type": "RIFLE", "title": "AK-74", "baseId": 12, "equipped": true}],
          "armors": [{"type": "ARMOR", "title": "Jacket", "baseId": 3}],
          "bullets": [{"type": "BULLET", "title": "5.45", "baseId": 40, "quantity": 30}],
          "items": [{"type": "MEDICINE", "title": "Medkit", "baseId": 60, "quantity": 2}]
        }
    """.trimIndent()

    @Test
    fun `enum names and parameters map onto the model`() {
        val model = Gson().fromJson(json, StoryDataDto::class.java).toModel()

        assertEquals(Gang.DUTY, model.gang)
        assertEquals(mapOf("met_barman" to 1), model.parametersMap)
        assertEquals(ItemType.RIFLE, model.weapons.single().type)
        assertEquals(ItemType.BULLET, model.bullets.single().type)
        assertEquals(ItemType.MEDICINE, model.items.single().type)
    }
}
