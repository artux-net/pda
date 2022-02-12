package net.artux.pda.map.engine;

public class Triple<K, T, S> {

    private final K first;
    private final T second;
    private final S third;

    public Triple(K first, T second, S third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public K getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public S getThird() {
        return third;
    }
}
