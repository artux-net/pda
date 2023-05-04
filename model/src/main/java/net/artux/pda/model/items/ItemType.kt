package net.artux.pda.model.items

enum class ItemType {
    ARMOR {
        override val typeId: Int
            get() = 4
        override val isWearable: Boolean
            get() = true
        override val isCountable: Boolean
            get() = false
    },
    PISTOL {
        override val typeId: Int
            get() = 0

        override val isWearable: Boolean
            get() = true

        override val isCountable: Boolean
            get() = false
    },
    RIFLE {
        override val isWearable: Boolean
            get() = true
        override val isCountable: Boolean
            get() = false

        override val typeId: Int
            get() = 1
    },
    MEDICINE {
        override val typeId: Int
            get() = 6
    },
    ARTIFACT {
        override val typeId: Int
            get() = 3
        override val isWearable: Boolean
            get() = true
    },
    DETECTOR {
        override val isWearable: Boolean
            get() = true
        override val isCountable: Boolean
            get() = false
        override val typeId: Int
            get() = 5
    },
    BULLET {
        override val typeId: Int
            get() = 2
    },
    ITEM {
        override val typeId: Int
            get() = 7
    };

    abstract val typeId: Int
    open val isWearable: Boolean
        get() = false
    open val isCountable: Boolean
        get() = true

    companion object {
        fun getByTypeId(typeId: Int): ItemType {
            for (type in values()) {
                if (type.typeId == typeId) return type
            }
            return ITEM
        }
    }
}