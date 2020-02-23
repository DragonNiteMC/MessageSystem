package com.ericlam.mc.msgsystem.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CircularIterator<E> implements Iterator<E> {


    private List<E> list;
    private int index;

    public CircularIterator(Collection<E> collection) {
        this.list = new LinkedList<>(collection);
    }

    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }

    @Override
    public E next() {
        if (list.isEmpty()) return null;
        try {
            return list.get(index++);
        } catch (IndexOutOfBoundsException e) {
            index = 0;
            return list.get(index++);
        }
    }
}
