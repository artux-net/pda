package net.artux.pdalib;

import java.util.ArrayList;

public class LimitedArrayList <Object> extends ArrayList<Object> {

    int limitMessages = 150;

    @Override
    public boolean add(Object e) {
        if (this.size() >= limitMessages) {
            this.remove(0); // delete first element
        }
        return super.add(e);
    }
}