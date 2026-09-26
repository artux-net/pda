package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.ItemsContainer (GET api/v1/items/all) - the item template
// catalog StrengthUpdater/ItemsGenerator draw from to equip stalkers and generate loot. Without
// it those lists are empty and every draw comes back null (a NullPointerException where callers
// force-cast the result, e.g. StrengthUpdater.updateStalker's "as WeaponModel").
data class ItemsContainerDto(
    @SerializedName("armors") val armors: List<ArmorDto>? = null,
    @SerializedName("weapons") val weapons: List<WeaponDto>? = null,
    @SerializedName("artifacts") val artifacts: List<ArtifactCatalogDto>? = null,
    @SerializedName("bullets") val bullets: List<ItemDto>? = null,
    @SerializedName("usual") val usual: List<ItemDto>? = null,
    @SerializedName("medicines") val medicines: List<MedicineDto>? = null,
    @SerializedName("detectors") val detectors: List<DetectorCatalogDto>? = null
)

data class ArtifactCatalogDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("health") val health: Int? = null,
    @SerializedName("radio") val radio: Int? = null,
    @SerializedName("damage") val damage: Int? = null,
    @SerializedName("bleeding") val bleeding: Int? = null,
    @SerializedName("thermal") val thermal: Int? = null,
    @SerializedName("chemical") val chemical: Int? = null,
    @SerializedName("endurance") val endurance: Int? = null,
    @SerializedName("electric") val electric: Int? = null,
    @SerializedName("equipped") val equipped: Boolean? = null
)

data class MedicineDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("stamina") val stamina: Float? = null,
    @SerializedName("radiation") val radiation: Float? = null,
    @SerializedName("health") val health: Float? = null
)

data class DetectorCatalogDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    // Enum name ("BASIC"/"MIDDLE"/"PROFESSIONAL"), same wire convention as everything else here.
    @SerializedName("detectorType") val detectorType: String? = null,
    @SerializedName("equipped") val equipped: Boolean? = null
)
