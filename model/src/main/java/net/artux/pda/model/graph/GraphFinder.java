package net.artux.pda.model.graph;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class GraphFinder<T> {
    private final Graph<T> graph;

    public GraphFinder(Graph<T> graph) {
        this.graph = graph;
    }

    public Optional<T> breadthFirstTraversal(T root, Condition<T> condition) {
        Set<Vertex<T>> visited = new LinkedHashSet<>();
        Queue<Vertex<T>> queue = new LinkedList<>();
        Vertex<T> rootVertex = new Vertex<>(root);
        queue.add(rootVertex);
        visited.add(rootVertex);
        while (!queue.isEmpty()) {
            Vertex<T> currentVertex = queue.poll();
            for (Vertex<T> v : graph.getAdjVertices().get(currentVertex)) {
                if (!visited.contains(currentVertex)) {
                    if (condition.fit(currentVertex.getObject())) {
                        return Optional.of(currentVertex.getObject());
                    }
                    queue.add(v);
                }
            }
        }
        return Optional.empty();
    }

    interface Condition<T> {
        boolean fit(T object);
    }
}
