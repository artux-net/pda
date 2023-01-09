package net.artux.pda.map.engine.pathfinding.own;

import com.badlogic.gdx.utils.Array;

public class Digraph<T> {

    private final Array<Node<T>> nodes;

    public Digraph() {
        this.nodes = new Array<>();
    }

    private Node<T> addNode(T t) {
        Node<T> node = new Node<>(t);
        nodes.add(node);
        return node;
    }

    public Array<Node<T>> getNodes() {
        return nodes;
    }

    public Node<T> offer(T t) {
        for (Node<T> node : nodes) {
            if (t.equals(node.getObject()))
                return node;
        }
        return addNode(t);
    }
}
