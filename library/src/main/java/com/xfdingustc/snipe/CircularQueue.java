package com.xfdingustc.snipe;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Richard on 10/10/15.
 */
public class CircularQueue<E> extends ArrayBlockingQueue<E> {

    public CircularQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean add(E e) {
        if (remainingCapacity() == 0) {
            remove();
        }
        return super.add(e);
    }

    @Override
    public boolean offer(E e) {
        if (remainingCapacity() == 0) {
            poll();
        }
        return super.offer(e);
    }
}
