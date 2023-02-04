package net.artux.engine.pathfinding.own;

import java.util.LinkedHashSet;
import java.util.Set;

public class GraphPath<T> {

    private final LinkedHashSet<Connection<T>> connections;
    private int cost = 0;

    public GraphPath() {
        this.connections = new LinkedHashSet<>();
    }

    public GraphPath(Set<Connection<T>> set) {
        this.connections = new LinkedHashSet<>(set);
    }

    public void addConnection(Connection<T> c) {
        connections.add(c);
        cost += c.getCost();
    }

    public LinkedHashSet<Connection<T>> getConnections() {
        return connections;
    }

    public int getCost() {
        return cost;
    }

    public GraphPath<T> cpy(){
        return new GraphPath<>(connections);
    }
}
