package net.artux.pda.model

import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.quest.story.StoryDataModel
import java.util.Collections
import java.util.LinkedList
import java.util.stream.Collectors

object QuestUtil {
    @JvmStatic
    fun check(
        conditions: HashMap<String?, List<String>?>?,
        storyDataModel: StoryDataModel
    ): Boolean {
        if (conditions != null) {
            if (conditions.keys.isNotEmpty()) {
                val map = storyDataModel.parametersMap
                for (condition in conditions.keys) {
                    when (condition) {
                        "has" -> if (conditions[condition] != null) for (has in conditions[condition]!!) {
                            if (!map.containsKey(has)
                                && !containsItem(storyDataModel.allItems, has)
                            ) return false
                        }

                        "!has" -> if (conditions[condition] != null) for (has in conditions[condition]!!) {
                            if (map.containsKey(has)
                                || containsItem(storyDataModel.allItems, has)
                            ) return false
                        }

                        "money>=" -> if (conditions[condition] != null) for (money in conditions[condition]!!) {
                            if (storyDataModel.money < money.toInt()) return false
                        }

                        "money<" -> if (conditions[condition] != null) for (money in conditions[condition]!!) {
                            if (storyDataModel.money >= money.toInt()) return false
                        }
                    }
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

    fun isInteger(str: String?): Boolean {
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

    fun difference(oldData: StoryDataModel, newData: StoryDataModel): Map<String, List<String>> {
        val oldItems = oldData.allItems
        val itemDifferences: MutableList<String> = LinkedList()
        val newItems = newData.allItems
            .stream()
            .filter { itemModel: ItemModel -> itemModel.id == null }
            .map { itemModel: ItemModel -> itemModel.baseId.toString() + ":" + itemModel.quantity }
            .collect(Collectors.toList())
        val response = HashMap(Collections.singletonMap("add_items", newItems))
        for (newItem in newData.allItems) {
            val difference = oldItems.stream()
                .filter { itemModel: ItemModel -> itemModel.id != null && itemModel.id == newItem.id && itemModel.quantity != newItem.quantity }
                .findFirst()
            difference.ifPresent { itemModel: ItemModel? -> itemDifferences.add(newItem.id.toString() + ":" + newItem.quantity) }
        }
        response["item"] = itemDifferences
        return response
    }
}