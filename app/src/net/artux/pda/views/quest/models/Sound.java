package net.artux.pda.views.quest.models;

public class Sound {

    private int id;
    private int type;
    private String name;
    private String url;
    private String[] params;

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sound sound = (Sound) o;
        return id == sound.id &&
                type == sound.type &&
                name.equals(sound.name) &&
                url.equals(sound.url);
    }

}
