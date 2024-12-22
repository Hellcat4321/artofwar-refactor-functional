package com.mygdx.game.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListUtils {
    public static <T> List<T> replaceObject(List<T> list, T oldObject, T newObject){
        List<T> newList = new ArrayList<>(list);
        int index = newList.indexOf(oldObject);
        list.set(index, newObject);
        return newList;
    }

    public static <T> List<T> addObject(List<T> list, T newObject){
        List<T> newList = new ArrayList<>(list);
        if(list.contains(newObject)) return new ArrayList<>(list);
        newList.add(newObject);
        return newList;
    }

    public static <T> List<T> removeObject(List<T> list, List<T> objectsToRemove) {
        List<T> newList = new ArrayList<>(list);
        newList.removeAll(objectsToRemove);
        return newList;
    }

    public static <T> List<T> forEach(List<T> list, Consumer<T> function){
        List<T> newList = new ArrayList<>(list);
        newList.forEach(function);
        return newList;
    }
}
