package net.artux.pda.model.graph;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Graph<T> {
    private final Map<Vertex<T>, HashSet<Vertex<T>>> adjVertices;

    public Graph() {
        this.adjVertices = new LinkedHashMap<>();
    }

    public Graph(Map<Vertex<T>, HashSet<Vertex<T>>> adjVertices) {
        this.adjVertices = adjVertices;
    }

    public Map<Vertex<T>, HashSet<Vertex<T>>> getAdjVertices() {
        return adjVertices;
    }

    void addVertex(T vertexObject) {
        adjVertices.putIfAbsent(new Vertex<>(vertexObject), new HashSet<>());
    }

    void removeVertex(T vertexObject) {
        Vertex<T> v = new Vertex<>(vertexObject);
        adjVertices.values().forEach(e -> e.remove(v));
        adjVertices.remove(new Vertex<>(vertexObject));
    }

    void addEdge(T from, T to) {
        Vertex<T> v1 = new Vertex<>(from);
        Vertex<T> v2 = new Vertex<>(to);
        adjVertices.get(v1).add(v2);
    }

    void removeEdge(T from, T to) {
        Vertex<T> v1 = new Vertex<>(from);
        Vertex<T> v2 = new Vertex<>(to);
        Set<Vertex<T>> eV1 = adjVertices.get(v1);
        Set<Vertex<T>> eV2 = adjVertices.get(v2);
        if (eV1 != null)
            eV1.remove(v2);
        if (eV2 != null)
            eV2.remove(v1);
    }

}
