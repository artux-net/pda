package net.artux.pda.map.engine.pathfinding.own;

public interface GraphPathFinder<T> {

    GraphPath<T> find(Digraph<T> graph, Node<T> source, Node<T> end);

}
