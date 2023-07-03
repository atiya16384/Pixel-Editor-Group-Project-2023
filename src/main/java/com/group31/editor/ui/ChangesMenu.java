package com.group31.editor.ui;

import java.util.ArrayList;
import javax.swing.JMenuItem;

import com.group31.editor.data.HistoryManager;

public class ChangesMenu extends javax.swing.JMenu {
    private ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
    private HistoryManager history = HistoryManager.getInstance();
    private static ChangesMenu menu; //hacky quick fix :)

    public ChangesMenu() {
        super("Restore...");
        if (menu == null)
            menu = this;
    }

    private void generateMenu() {
        int size = history.sizeof() > 10 ? 10 : history.sizeof();
        if (size == 0) {
            setEnabled(false);
            return;
        }
        
        setEnabled(true);
        String[] changes = history.getProjectChanges(size);
        int i = 1;

        for (String change : changes) {
            final int thisChange = i;
            JMenuItem item = new JMenuItem(change);
            item.addActionListener(e -> {
                history.restoreChanges(thisChange);
            });
            items.add(item);
            this.add(item);
            i++;
        }
    }

    private void cleanMenu() {
        for (JMenuItem item : items) {
            this.remove(item);
        }
        items.clear();
    }

    public static void triggerUpdate() {
        menu.cleanMenu();
        menu.generateMenu();
    }
}
