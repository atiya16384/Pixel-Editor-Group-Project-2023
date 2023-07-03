package com.group31.editor.ui.options;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.selection.Selection;
import com.group31.editor.canvas.selection.SelectionListener;
import com.group31.editor.canvas.transform.MoveTransformAction;

import javax.swing.*;

public class SelectionOptionsPanel extends OptionsPanel implements SelectionListener {

  private final JButton moveButton;

  public SelectionOptionsPanel() {
    this.moveButton = new JButton("Move");
    this.moveButton.setEnabled(false);
    this.moveButton.addActionListener(e -> {
      Canvas.getInstance().setTransformAction(new MoveTransformAction());
    });
    this.add(moveButton);

    Canvas.getInstance().addSelectionListener(this);
  }

  @Override
  public void selectionChanged(Selection selection) {
    var transformEnabled = selection != null && selection.getEndPoint() != null;
    moveButton.setEnabled(transformEnabled);
  }
}
