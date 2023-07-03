package com.group31.editor.ui.headless;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
// import java.awt.*;
// import javax.swing.*;
// import javax.swing.border.*;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.*;

public class JPEGOptions extends javax.swing.JDialog {

  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JLabel label = new JLabel();
  private JSlider qualitySlider = new JSlider();
  private JPanel buttonBar = new JPanel();
  private JButton okButton = new JButton();

  public JPEGOptions() {
    // public JPEGOptions(Frame frame) {
    // super(frame, "JPEG Export Options", null);

    //======== this ========
    setTitle("JPEG Export Options");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    dialogPane.setLayout(new BorderLayout());

    //======== contentPanel ========
    contentPanel.setLayout(
      new MigLayout(
        "insets 0,hidemode 3",
        // columns
        "[174,fill]" + "[grow,fill]",
        // rows
        "[86]"
      )
    );

    //---- label ----
    label.setText("Quality");
    contentPanel.add(label, "cell 0 0");
    contentPanel.add(qualitySlider, "cell 1 0");

    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] { 0, 80 };
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] { 1.0, 0.0 };

    //---- okButton ----
    okButton.setText("OK");
    buttonBar.add(
      okButton,
      new GridBagConstraints(
        1,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0),
        0,
        0
      )
    );

    dialogPane.add(buttonBar, BorderLayout.SOUTH);
    dialogPane.add(contentPanel, BorderLayout.CENTER);
    contentPane.add(dialogPane, BorderLayout.CENTER);
    pack();
    // setLocationRelativeTo(getOwner());
  }

  public float getQuality() {
    setVisible(true);
    return qualitySlider.getValue() / 100;
  }
}
