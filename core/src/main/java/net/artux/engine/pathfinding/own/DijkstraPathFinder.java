package net.artux.engine.pathfinding.own;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DijkstraPathFinder<T> implements GraphPathFinder<T> {

    @Override
    public GraphPath<T> find(Digraph<T> graph, Node<T> source, Node<T> end) {
        if (!graph.getNodes().contains(source, true)
                || source.getConnections().size == 0)
            return null;

        Set<Node<T>> visitedNodes = new HashSet<>();
        Map<Node<T>, GraphPath<T>> frontierPathMap = new HashMap<>();
        frontierPathMap.put(source, new GraphPath<>());

        Node<T> currentNode = source;
        float minCost = Float.MAX_VALUE;
        Node<T> minCostNode = source;

        while (visitedNodes.size() < graph.getNodes().size) {
            visitedNodes.add(currentNode);
            for (Connection<T> connection : currentNode.getConnections()) {
                Node<T> target = connection.getTarget();

                GraphPath<T> suggestedPath;
                if (!frontierPathMap.containsKey(target)) {
                    suggestedPath = frontierPathMap.get(currentNode).cpy();
                    suggestedPath.addConnection(connection);
                    frontierPathMap.put(target, suggestedPath);
                } else {
                    GraphPath<T> oldPath = frontierPathMap.get(target);
                    suggestedPath = frontierPathMap.get(currentNode).cpy();
                    suggestedPath.addConnection(connection);

                    if (oldPath.getCost() > suggestedPath.getCost())
                        frontierPathMap.put(target, suggestedPath);
                }

                if (suggestedPath.getCost() < minCost) {
                    minCostNode = target;
                    minCost = suggestedPath.getCost();
                }

                if (target == end)
                    return frontierPathMap.get(end);
            }
            currentNode = minCostNode;
        }

        return null;
    }
}
