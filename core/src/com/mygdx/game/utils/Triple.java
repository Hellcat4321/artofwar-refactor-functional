package com.mygdx.game.utils;

public final class Triple<T, U, O> {

    public Triple(T first, U second, O third) {
        this.second = second;
        this.first = first;
        this.third=third;
    }

    public final T first;
    public final U second;
    public final O third;

    public static <T, U, O> Triple<T, U, O> triple(T first, U second, O third) {
        return new Triple<>(first, second, third);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", "+third+")";
    }
}
