package net.artux.pda.model.items;

public enum ItemType {
    ARMOR {
        @Override
        public int getTypeId() {
            return 4;
        }

        @Override
        public boolean isWearable() {
            return true;
        }

        @Override
        public boolean isCountable() {
            return false;
        }
    },
    PISTOL {
        @Override
        public int getTypeId() {
            return 0;
        }

        @Override
        public boolean isWearable() {
            return true;
        }

        @Override
        public boolean isCountable() {
            return false;
        }
    },
    RIFLE {
        @Override
        public int getTypeId() {
            return 1;
        }

        @Override
        public boolean isWearable() {
            return true;
        }

        @Override
        public boolean isCountable() {
            return false;
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

        @Override
        public boolean isWearable() {
            return true;
        }
    },
    DETECTOR {
        @Override
        public int getTypeId() {
            return 5;
        }

        @Override
        public boolean isWearable() {
            return true;
        }

        @Override
        public boolean isCountable() {
            return false;
        }
    },
    BULLET {
        @Override
        public int getTypeId() {
            return 2;
        }

    },
    ITEM {
        @Override
        public int getTypeId() {
            return 7;
        }

    };

    public abstract int getTypeId();

    public static ItemType getByTypeId(int typeId) {
        for (ItemType type : ItemType.values()) {
            if (type.getTypeId() == typeId)
                return type;
        }
        return ItemType.ITEM;
    }

    public boolean isWearable() {
        return false;
    }

    public boolean isCountable() {
        return true;
    }
}
