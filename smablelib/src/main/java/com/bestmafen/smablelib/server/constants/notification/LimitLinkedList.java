package com.bestmafen.smablelib.server.constants.notification;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Administrator on 2018/5/19/019.
 * 限定长度的队列，超过指定长度时，删除最先添加的元素
 */
public class LimitLinkedList<E> extends LinkedList<E> {
    private int mLimit;

    public LimitLinkedList(int limit) {
        super();
        this.mLimit = limit;
    }

    @Override
    public boolean add(E o) {
        if (size() == mLimit) {
            removeFirst();
        }
        return super.add(o);
    }

    @Override
    public void addLast(E e) {
        if (size() == mLimit) {
            removeFirst();
        }
        super.addLast(e);
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }
}
