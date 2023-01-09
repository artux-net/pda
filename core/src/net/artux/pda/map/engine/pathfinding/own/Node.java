package net.artux.pda.map.engine.pathfinding.own;

import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Node<T> {

    private final T object;
    private final Array<Connection<T>> connections;

    public Node(T object, int initialConnectionCapacity) {
        this.object = object;
        connections = new Array<>(initialConnectionCapacity);
    }

    public Node(T object) {
        this.object = object;
        connections = new Array<>();
    }

    public Connection<T> addConnection(Node<T> node, int cost) {
        Connection<T> connection = new Connection<>(this, node, cost);
        connections.add(connection);
        return connection;
    }

    public Array<Connection<T>> getConnections() {
        return connections;
    }

    public Collection<Node<T>> getConnectedNodes() {
        return Arrays.stream(connections.items).map(Connection::getTarget).collect(Collectors.toSet());
    }

    public T getObject() {
        return object;
    }
}
