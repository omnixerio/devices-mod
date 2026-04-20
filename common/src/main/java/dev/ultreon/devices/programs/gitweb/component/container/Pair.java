package dev.ultreon.devices.programs.gitweb.component.container;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair() {

    }

    public Pair(Pair<F, S> pair) {
        this.first = pair.first;
        this.second = pair.second;
    }

    public Pair<S, F> swap() {
        return new Pair<>(second, first);
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
