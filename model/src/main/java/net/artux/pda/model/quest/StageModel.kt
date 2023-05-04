package net.artux.pda.model.quest

class StageModel {
    var id = 0
    var title: String? = null
    var content: String? = null
    var type: StageType? = null
    var transfers: List<TransferModel>? = null
}