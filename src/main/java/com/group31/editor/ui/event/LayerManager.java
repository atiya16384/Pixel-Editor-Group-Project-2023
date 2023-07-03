package com.group31.editor.ui.event;

import com.group31.editor.ui.LayerSelector;

public class LayerManager extends Thread {
    private LayerSelector selector;
    public static Boolean change = false;

    public LayerManager(LayerSelector selector) {
        this.selector = selector;
    }

    public void updateSelector(LayerSelector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("LayerManager");
        try {
            while (true) {
                synchronized (change) {
                    change.wait();
                    selector.refresh();
                }
            }
        } catch (InterruptedException e) {}
    }

    public static synchronized void setChange() {
        synchronized (change) {
            change.notifyAll();
        }
    }
}
