package org.example.ThreadPool;

import java.util.LinkedList;
import java.util.List;

public class ReaderThreadPool {
    private final List<Thread> threads = new LinkedList<>();
    private final BlockingStorage tasksQueue = new BlockingStorage();


    public ReaderThreadPool() {
        int size = 9;
        while (size-- > 0) {
            threads.add(new Thread(this::executeImpl));
        }
    }

    public void interrupt(){
        for (var thread: threads){
            thread.interrupt();
        }
    }

    public void execute(Runnable task) {
        tasksQueue.put(task);
    }

    public void start() {
        for (var thread : threads) {
            thread.start();
        }
    }

    private void executeImpl() {
        var task = (Runnable) tasksQueue.take();
        while (!Thread.interrupted()) {
            task.run();
            task = (Runnable) tasksQueue.take();
        }
    }
}
