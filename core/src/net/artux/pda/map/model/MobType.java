package net.artux.pda.map.model;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.engine.ContentGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobType {

    public int id;
    public String name;
    public int group;
    public List<Integer> armor = new ArrayList<>();
    public List<String> weapon = new ArrayList<>();
    public List<String> items = new ArrayList<>();
    public int travel;

    MobType() {
        id = -1;
        name = "Undefined";
        group = 0;
        travel = 0;
    }

    enum Group {
        STALKER {
            @Override
            int getId() {
                return 0;
            }
        },
        BANDIT {
            @Override
            int getId() {
                return 1;
            }
        },
        MILITARY {
            @Override
            int getId() {
                return 2;
            }
        },
        FREEDOM {
            @Override
            int getId() {
                return 3;
            }
        },
        DUTY {
            @Override
            int getId() {
                return 4;
            }
        },
        MONOLITH {
            @Override
            int getId() {
                return 5;
            }
        },
        MERCENARY {
            @Override
            int getId() {
                return 6;
            }
        },
        MUTANTS {
            @Override
            int getId() {
                return 7;
            }
        };

        abstract int getId();

        public static int getRelation(Group from, Group to) {
            if (relations.containsKey(from.getId())) {
                if (relations.get(from.getId()).size() > to.getId())
                    return relations.get(from.getId()).get(to.getId());
            }
            return -5;
        }

        private static HashMap<Integer, ArrayList<Integer>> relations;

        static {
            JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
            MobsTypes mobsTypes = new Gson().fromJson(reader, MobsTypes.class);
            relations = mobsTypes.relations;
        }

    }
}
