package net.artux.pda.model.graph;

import java.util.Objects;

public class Vertex<T> {
    private final T object;

    public Vertex(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(object, vertex.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }
}
