package net.artux.pda.model.map

enum class Strength(val titleId: String, val price: Int) {
    WEAK("gang.strength.weak", 0),
    STALKER("gang.strength.stalker", 5000),
    MIDDLE("gang.strength.middle", 10000),
    MASTER("gang.strength.master", 15000),
    STRONG("gang.strength.strong", 30000);

    fun getNext(): Strength {
        return when (this) {
            STALKER -> MIDDLE
            MIDDLE -> MASTER
            MASTER -> STRONG
            STRONG -> STRONG
            else -> STALKER
        }
    }
}