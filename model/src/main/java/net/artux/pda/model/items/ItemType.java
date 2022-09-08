package net.artux.pda.model.items;

public enum ItemType {
    ARMOR {
        @Override
        public int getTypeId() {
            return 4;
        }
        },
    PISTOL {
        @Override
        public int getTypeId() {
            return 0;
        }

    },
    RIFLE {
        @Override
        public int getTypeId() {
            return 1;
        }

    },
    MEDICINE {
        @Override
        public int getTypeId() {
            return 6;
        }

    },
    ARTIFACT {
        @Override
        public int getTypeId() {
            return 3;
        }

    },
    DETECTOR {
        @Override
        public int getTypeId() {
            return 5;
        }

    },
    BULLET {
        @Override
        public int getTypeId() {
            return 2;
        }

    };

    public abstract int getTypeId();

    public static ItemType getByTypeId(int typeId) {
        for (ItemType type : ItemType.values()) {
            if (type.getTypeId() == typeId)
                return type;
        }
        return ItemType.BULLET;
    }
}
