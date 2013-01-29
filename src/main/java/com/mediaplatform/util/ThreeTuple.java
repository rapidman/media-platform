package com.mediaplatform.util;

/**
 * User: sergey
 * Date: 15/06/2009
 * Time: 4:35:04 PM
 */
public class ThreeTuple<A,B, C> extends TwoTuple<A, B>{
    public final C third;

    public ThreeTuple(A first, B second, C third) {
        super(first, second);

        this.third = third;
    }

    public C getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;

        ThreeTuple that = (ThreeTuple) o;

        return !(third != null ? !third.equals(that.third) : that.third != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return  super.toString()+";"+third+")";
    }
}
