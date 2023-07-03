package com.group31.editor.ui;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.selection.Selection;
import com.group31.editor.canvas.selection.SelectionListener;
import com.group31.editor.canvas.transform.ClipboardTransformAction;
import com.group31.editor.canvas.transform.TransformAction;
import com.group31.editor.canvas.transform.TransformActionListener;
import com.group31.editor.ui.event.MenuEvent;
import java.awt.BorderLayout;
import javax.swing.*;

public class MenuBar extends JMenuBar implements SelectionListener, TransformActionListener {

  private JMenu fileMenu;
  private JMenu editMenu;
  private JMenu helpMenu;
  private JMenu exportMenu;
  private JMenu historyEvents;
  private JMenu viewMenu;

  private Canvas canvas = Canvas.getInstance();
  
  public MenuBar(UIFrame uiFrame) {
    this.exportMenu = new JMenu("Export To...");
    this.exportMenu.add(MenuEvent.EXPORT_BMP.getAction());
    this.exportMenu.add(MenuEvent.EXPORT_PNG.getAction());
    this.exportMenu.add(MenuEvent.EXPORT_JPEG.getAction());
    this.exportMenu.add(MenuEvent.EXPORT_JPEG_QUALITY.getAction());    

    // 'File' menu
    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic('F');
    this.fileMenu.add(MenuEvent.NEW_PROJECT.getAction());
    this.fileMenu.add(MenuEvent.OPEN_PROJECT.getAction());
    this.fileMenu.add(MenuEvent.SAVE_PROJECT.getAction());
    this.fileMenu.add(MenuEvent.SAVE_PROJECT_AS.getAction());
    this.fileMenu.addSeparator();
    this.fileMenu.add(MenuEvent.OPEN_FILE.getAction());
    this.fileMenu.add(exportMenu);
    this.fileMenu.add(MenuEvent.SEND_FEEDBACK.getAction());
    add(fileMenu);

    this.editMenu = new JMenu("Edit");
    this.editMenu.setMnemonic('E');
    this.editMenu.add(MenuEvent.UNDO.getAction());
    this.editMenu.add(MenuEvent.REDO.getAction());
    this.editMenu.add(new ChangesMenu());
    this.editMenu.addSeparator();
    this.editMenu.add(MenuEvent.COPY.getAction());
    this.editMenu.add(MenuEvent.CUT.getAction());
    this.editMenu.add(MenuEvent.PASTE.getAction());
    this.editMenu.addSeparator();
    this.editMenu.add(MenuEvent.FILL_BG.getAction());
    add(editMenu);

    // "View" menu
    this.viewMenu = new JMenu("View");
    this.editMenu.setMnemonic('E');
    this.viewMenu.add(MenuEvent.GRID_LINES.getAction());
    this.viewMenu.add(MenuEvent.TOGGLE_TP.getAction());
    //  this.viewMenu.add(MenuEvent.ZOOM_IN.getAction()); // TODO
    //  this.viewMenu.add(MenuEvent.ZOOM_OUT.getAction());
    add(viewMenu);

    uiFrame.getJFrame().add(this, BorderLayout.PAGE_START);

    MenuEvent.COPY.getAction().setEnabled(false);
    MenuEvent.CUT.getAction().setEnabled(false);
    MenuEvent.PASTE.getAction().setEnabled(false);
    canvas.addSelectionListener(this);
    canvas.addTransformActionListener(this);
  }

  @Override
  public void selectionChanged(Selection selection) {
    var enableClipboard = selection != null && (!(Canvas.getInstance().getTransformAction() instanceof ClipboardTransformAction cta) || !cta.isPasting());
    MenuEvent.COPY.getAction().setEnabled(enableClipboard);
    MenuEvent.CUT.getAction().setEnabled(enableClipboard);
  }

  @Override
  public void transformActionChanged(TransformAction transformAction) {
    if (transformAction instanceof ClipboardTransformAction cta) {
      var enableClipboard = !cta.isPasting() && Canvas.getInstance().getSelection() != null;
      MenuEvent.PASTE.getAction().setEnabled(!cta.isPasting());
      MenuEvent.COPY.getAction().setEnabled(enableClipboard);
      MenuEvent.CUT.getAction().setEnabled(enableClipboard);
    } else {
      MenuEvent.PASTE.getAction().setEnabled(false);
    }
  }

}
