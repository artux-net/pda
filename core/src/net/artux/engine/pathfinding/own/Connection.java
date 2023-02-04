package net.artux.engine.pathfinding.own;

public class Connection<T> {

    private final Node<T> source;
    private final Node<T> target;
    private final float cost;
    private Object userObject;

    public Connection(Node<T> source, Node<T> target, int cost) {
        this.source = source;
        this.target = target;
        this.cost = cost;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public Object getUserObject() {
        return userObject;
    }

    public float getCost() {
        return cost;
    }

    public Node<T> getTarget() {
        return target;
    }

    public Node<T> getSource() {
        return source;
    }
}
