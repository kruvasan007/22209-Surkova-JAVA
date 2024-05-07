package org.example.ThreadPool;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingStorage {
    private final Queue<Object> queue = new LinkedList<>();

    public void put(Object object) {
        synchronized (queue)
        {
            queue.add(object);
            queue.notify();
        }
    }

    public Object take() {
        synchronized (queue)
        {
            while (queue.isEmpty())
            {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return queue.poll();
        }
    }
}
