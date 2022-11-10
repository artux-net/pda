package net.artux.pda.map.model;

import net.artux.pda.map.engine.components.MoodComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BotType {

    public int id;
    public String name;
    public int group;
    public Integer[] relations;
    public List<Integer> armor = new ArrayList<>();
    public List<String> weapon = new ArrayList<>();
    public List<String> items = new ArrayList<>();
    public int travel;

    BotType() {
        id = -1;
        name = "Undefined";
        group = 0;
        travel = 0;
    }

    public MoodComponent getMood(Set<String> params) {
        return new MoodComponent(id, relations, params);
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

        /*public static int getRelation(Group from, Group to) {
            if (relations.containsKey(from.getId())) {
                if (relations.get(from.getId()).size() > to.getId())
                    return relations.get(from.getId()).get(to.getId());
            }
            return -5;
        }*/

        //private static HashMap<Integer, ArrayList<Integer>> relations;

        /*static {
            JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
            BotsTypes botsTypes = new Gson().fromJson(reader, BotsTypes.class);
            relations = botsTypes.relations;
        }*/

    }
}
