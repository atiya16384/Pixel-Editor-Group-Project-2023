package com.group31.editor.ui.headless;

import com.group31.editor.data.*;
import com.group31.editor.libs.SpringUtilities;
import com.group31.editor.libs.SvgImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class NewProjectDialog extends JDialog implements ActionListener {

  private String projectName;
  private Integer x;
  private Integer y;

  private JTextField titleField;
  private JTextField widthField;
  private JTextField heightField;

  public NewProjectDialog(java.awt.Frame parent) {
    super(parent, "New Project", true);
    JPanel panel = new JPanel(new SpringLayout());

    this.titleField = new JTextField();
    this.widthField = new JTextField();
    this.heightField = new JTextField();

    JPanel entry = new JPanel(new SpringLayout());
    entry.add(new JLabel("Project Name: "));
    entry.add(titleField);
    entry.add(new JLabel("Project Height: "));
    entry.add(heightField);
    entry.add(new JLabel("Project Width: "));
    entry.add(widthField);
    SpringUtilities.makeCompactGrid(
      entry,
      3,
      2, // rows, columns
      5,
      5, // initX, initY
      5,
      5
    ); // xPad, yPad

    JButton okButton = new JButton(
      "OK",
      SvgImageIcon.createIconFromSvg("popup/confirm.svg", new Dimension(10, 10))
    );
    okButton.setActionCommand("ok_button");
    okButton.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
    okButton.addActionListener(this);

    JButton cancelButton = new JButton(
      "Cancel",
      SvgImageIcon.createIconFromSvg("popup/close.svg", new Dimension(10, 10))
    );
    cancelButton.setActionCommand("cancel_button");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(this);

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(okButton);
    buttons.add(cancelButton);

    panel.add(entry, BorderLayout.NORTH);
    panel.add(buttons, BorderLayout.SOUTH);
    SpringUtilities.makeCompactGrid(
      panel,
      2,
      1, // rows, columns
      5,
      5, // initX, initY
      5,
      5
    ); // xPad, yPad

    getRootPane().setDefaultButton(okButton);
    add(panel);
    setSize(300, 180);
    setLocationRelativeTo(null);
		setAlwaysOnTop(true);
    setResizable(false);
  }

  public FileSchema getOutcome() {
    if (this.x == null || this.y == null) return null;
    return new FileSchema(projectName, x, y);
  }

  private boolean warned = false;
  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    switch (cmd) {
      case "ok_button":
        this.projectName = this.titleField.getText();
        try {
          if(Integer.parseInt(this.heightField.getText()) < 1 || Integer.parseInt(this.widthField.getText()) < 1){
            JOptionPane.showMessageDialog(this, "You must enter a number larger than 0");
            return;
          }else if((Integer.parseInt(this.heightField.getText()) > 5000 || Integer.parseInt(this.widthField.getText()) > 5000) && !warned){
            JOptionPane.showMessageDialog(this, "There is a rendering issue with this height, you may crash, click OK to continue anyway.");
            warned = true;
          } else{
            this.x = Integer.parseInt(this.widthField.getText());
            this.y = Integer.parseInt(this.heightField.getText());
          }
        } catch (NumberFormatException err) {
          JOptionPane.showMessageDialog(this, "Please enter a valid number");
          return;
        }
        this.dispose();
        break;
      case "cancel_button":
        this.dispose();
        break;
    }
  }
}
