package net.artux.pda.model.items

data class ArtifactModel(
    var anomalyId: Int = 0,
    var health: Int = 0,
    var radio: Int = 0,
    var damage: Int = 0,
    var bleeding: Int = 0,
    var thermal: Int = 0,
    var chemical: Int = 0,
    var endurance: Int = 0,
    var electric: Int = 0,
) : WearableModel() {

}