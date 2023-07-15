package net.artux.pda.model

import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.quest.story.StoryDataModel
import java.util.LinkedList
import java.util.stream.Collectors

object QuestUtil {
    @JvmStatic
    fun check(
        conditions: HashMap<String, List<String>>?,
        storyDataModel: StoryDataModel
    ): Boolean {
        if (conditions.isNullOrEmpty())
            return true
        val map = storyDataModel.parametersMap
        for (key in conditions.keys) {
            if (conditions[key].isNullOrEmpty())
                continue

            when (key) {
                "has" -> for (param in conditions[key]!!) {
                    if (!map.containsKey(param)
                        && !containsItem(storyDataModel.allItems, param)
                    ) return false
                }

                "!has" -> for (param in conditions[key]!!) {
                    if (map.containsKey(param)
                        || containsItem(storyDataModel.allItems, param)
                    ) return false
                }

                "money>=" -> for (param in conditions[key]!!) {
                    if (storyDataModel.money < param.toInt()) return false
                }

                "money<" -> for (param in conditions[key]!!) {
                    if (storyDataModel.money >= param.toInt()) return false
                }

                "reachedStage" -> {

                }

            }
        }
        return true
    }

    private fun containsItem(items: List<ItemModel>, sid: String): Boolean {
        if (isInteger(sid)) {
            val id = sid.toInt()
            for (item in items) {
                if (item.baseId == id) return true
            }
        }
        return false
    }

    private fun isInteger(str: String?): Boolean {
        if (str == null) {
            return false
        }
        val length = str.length
        if (length == 0) {
            return false
        }
        var i = 0
        if (str[0] == '-') {
            if (length == 1) {
                return false
            }
            i = 1
        }
        while (i < length) {
            val c = str[i]
            if (c < '0' || c > '9') {
                return false
            }
            i++
        }
        return true
    }

    fun calculateDifference(oldData: StoryDataModel, newData: StoryDataModel): LinkedHashMap<String, List<String>> {
        val response = LinkedHashMap<String, List<String>>()

        val oldItems = oldData.allItems
        val itemDifferences: MutableList<String> = LinkedList()
        //new items
        val newItems = newData.allItems
            .stream()
            .filter { itemModel: ItemModel -> itemModel.id == null }
            .map { itemModel: ItemModel -> itemModel.baseId.toString() + ":" + itemModel.quantity }
            .collect(Collectors.toList())
        response["item"] = newItems

        // quantity and quality difference
        for (newItem in newData.allItems) {
            val quantityDifference = oldItems.stream()
                .filter { itemModel: ItemModel ->
                    itemModel.id != null
                            && itemModel.id == newItem.id
                            && itemModel.quantity != newItem.quantity
                }
                .findFirst()
            quantityDifference.ifPresent { itemDifferences.add(newItem.id.toString() + ":" + newItem.quantity) }
        }
        response["item"] = itemDifferences

        // money difference
        response["money"] = listOf((newData.money - oldData.money).toString())

        //xp difference
        response["xp"] = listOf((newData.xp - oldData.xp).toString())

        // wearable difference
        val wearableItems = mutableListOf<String>()
        newData.allItems.stream()
            .filter { itemModel: ItemModel -> itemModel.type.isWearable }
            .map { it: ItemModel -> it as WearableModel }
            .forEach {
                if (it.isEquipped)
                    if (it.id == null) {
                        wearableItems.add(it.baseId.toString())
                    } else {
                        wearableItems.add(it.id.toString())
                    }
            }

        oldItems.stream()
            .filter { itemModel: ItemModel -> itemModel.type.isWearable }
            .map { it: ItemModel -> it as WearableModel }
            .forEach { it ->
                if (it.isEquipped) {
                    val id = it.id.toString()
                    wearableItems.removeIf { it == id }
                }
            }
        response["set"] = wearableItems

        return response
    }
}