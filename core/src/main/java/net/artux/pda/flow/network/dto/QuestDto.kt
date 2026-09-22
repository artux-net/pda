package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.StoryInfo (GET api/v1/quest - the selection list).
data class StoryInfoDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("desc") val desc: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("needs") val needs: List<Int>? = null
)

// Mirrors net.artux.pdanetwork.model.StoryDto (GET api/v1/quest/{id} - the whole compiled
// story: every chapter and every stage in it, nested).
data class StoryDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("chapters") val chapters: Map<String, ChapterDto>? = null
)

// Mirrors net.artux.pdanetwork.model.ChapterDto.
data class ChapterDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("stages") val stages: Map<String, StageDto>? = null
)

// Mirrors net.artux.pdanetwork.model.Stage (the network one, distinct from
// net.artux.pda.model.quest.Stage which is the mapped :model target).
data class StageDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("type_stage") val typeStage: Int? = null,
    @SerializedName("background") val background: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("type_message") val typeMessage: Int? = null,
    @SerializedName("texts") val texts: List<TextDto>? = null,
    @SerializedName("transfers") val transfers: List<TransferDto>? = null,
    @SerializedName("actions") val actions: Map<String, List<String>>? = null,
    @SerializedName("data") val data: Map<String, String>? = null
)

// Mirrors net.artux.pdanetwork.model.Transfer.
data class TransferDto(
    @SerializedName("stage") val stage: Long? = null,
    @SerializedName("condition") val condition: Map<String, List<String>>? = null,
    @SerializedName("text") val text: String? = null
)

// Mirrors net.artux.pdanetwork.model.Text.
data class TextDto(
    @SerializedName("text") val text: String? = null,
    @SerializedName("condition") val condition: Map<String, List<String>>? = null
)
