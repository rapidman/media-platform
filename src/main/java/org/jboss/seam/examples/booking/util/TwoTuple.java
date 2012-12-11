package org.jboss.seam.examples.booking.util;

import org.jboss.solder.core.Veto;

/**
 * User: sergey
 * Date: 20/04/2009
 * Time: 3:36:24 PM
 */
@Veto
public class TwoTuple<A, B> {
    public final A first;
    public final B second;

    public TwoTuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "("+first+";"+second+")";
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwoTuple twoTuple = (TwoTuple) o;

        if (first != null ? !first.equals(twoTuple.first) : twoTuple.first != null) return false;
        if (second != null ? !second.equals(twoTuple.second) : twoTuple.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
