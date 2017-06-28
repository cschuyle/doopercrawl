package dragnon.doopercrawl;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Link {

    private final ImmutablePair<String, String> pair;

    private Link(String from, String to) {
        this.pair = new ImmutablePair<String, String>(from, to);
    }

    static Link link(String from, String to) {
        return new Link(from, to);
    }

    String from() {
        return pair.getLeft();
    }

    String to() {
        return pair.getRight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        return pair != null ? pair.equals(link.pair) : link.pair == null;
    }

    @Override
    public int hashCode() {
        return pair != null ? pair.hashCode() : 0;
    }

    @Override
    public String toString() {
        return from() + " ==> " + to();
    }
}
