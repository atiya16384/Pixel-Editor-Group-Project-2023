package com.group31.editor.ui.headless;

import com.group31.editor.canvas.Canvas;
import com.group31.editor.canvas.error.InUseCanvasException;
import com.group31.editor.data.FileSchema;
import com.group31.editor.data.ProjectHandler;
import com.group31.editor.libs.SvgImageIcon;
import com.group31.editor.util.Logger;
import com.group31.editor.util.SentryReporting;
import com.group31.editor.util.SwingUtilities;
import com.group31.editor.data.FileSchema;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;
import java.awt.Container;
import java.nio.file.Path;

import io.sentry.Sentry;
import net.miginfocom.swing.MigLayout;

public class InitialProjectSelection extends JDialog implements java.awt.event.ActionListener {
    private JPanel dialogPane = new JPanel();
    private JPanel contentPanel = new JPanel();
    private JLabel image = new JLabel();
    private JPanel projectControls = new JPanel();
    private JButton load = new JButton();
    private JButton create = new JButton();
    private JToggleButton sentryToggle = new JToggleButton();

    private ProjectHandler ph = ProjectHandler.getInstance();
    private enum setupcases {LOAD, CREATE};
    private setupcases setupcase = null;

    public InitialProjectSelection(Frame parent) {
        super(parent, "New Project", true);
        setIconImage(SwingUtilities.loadImageFromResource("images/AppIcon.png"));
		setTitle("Pixel Editor v0.3 - Project Selection");
		setResizable(false);
		// setAlwaysOnTop(true);
		setLocationByPlatform(true);
		setFocusTraversalPolicyProvider(true);
		setLayout(new BorderLayout());

		//======== dialogPane ========
        dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
        contentPanel.setLayout(new MigLayout(
            "insets dialog,hidemode 3",
            // columns
            "[fill]" +
            "[fill]" +
            "[fill]",
            // rows
            "[]"));

        //---- image ----
        image.setIcon(SvgImageIcon.createIconFromSvg("images/welcome.svg", new java.awt.Dimension(300, 200)));
        contentPanel.add(image, "cell 0 0");

        //======== projectControls ========
        projectControls.setLayout(new MigLayout(
            "insets dialog,hidemode 3",
            // columns
            "[fill]",
            // rows
            "[]" +
            "[]" +
            "[62]" +
            "[]"));

                    //---- load ----
        load.setText("Open Project");
        load.setActionCommand("load");
        load.addActionListener(this);
        projectControls.add(load, "cell 0 0");

        //---- create ----
        create.setText("Create Project");
        create.setActionCommand("create");
        create.addActionListener(this);
        projectControls.add(create, "cell 0 1");

        //---- sentryToggle ----
        sentryToggle.setText("Tracking Opt-Out");
        sentryToggle.setActionCommand("sentry");
        sentryToggle.addActionListener(this);
        projectControls.add(sentryToggle, "cell 0 3");

        contentPanel.add(projectControls, "cell 1 0");
        dialogPane.add(contentPanel, BorderLayout.CENTER);
        add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public void run() {
        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String cmd = e.getActionCommand();
        SentryReporting.leaveABreadcrumb(
            SentryReporting.BREADCRUMB_TYPE.DEFAULT,
            "Project selection action",
            "core"
        );
        Logger.log(
            String.format("Project selection action: %s", cmd),
            Logger.LOG_TYPE.PROJECT
        );
        switch (cmd) {
            case "load":
                try {
                    FileDialog fd = new FileDialog(
                        "Open Project...",
                        new FileNameExtensionFilter("Editor Project", "epj"),
                        FileDialog.WindowType.OPEN,
                        FileDialog.FILES_ONLY
                    );
                    Path pt = fd.getResult();
                    ph.readProject(pt);
                } catch (java.io.IOException err) {
                    Logger.log("Failed to read file", Logger.LOG_TYPE.ERROR);
                    // TODO file fail to read error pop-up
                } catch (ClassNotFoundException err) {
                    Sentry.captureException(err);
                    // TODO program severe error pop-up
                    Logger.log(
                    "A severe error has occured, check the program is installed properly and try again.",
                    Logger.LOG_TYPE.ERROR
                    );
                    throw new RuntimeException(err);
                }
    
                Logger.log(
                    String.format("New Project, Data: %s", ProjectHandler.getInstance().getCurrent().getDetailsJSON()),
                    Logger.LOG_TYPE.PROJECT
                );

                this.setupcase = setupcases.LOAD;
                dispose();
                break;
            case "create":
                JFrame deadFrame = null;
                NewProjectDialog dialog = new NewProjectDialog(deadFrame);
                dialog.setVisible(true);
                dialog.setAutoRequestFocus(true);
                dialog.toFront();
            
                FileSchema temp = dialog.getOutcome();
                if (temp == null) System.exit(0);
                ph.loadSchema(temp);

                Logger.log(
                    "Creating new project",
                    Logger.LOG_TYPE.PROJECT
                );
                
                this.setupcase = setupcases.CREATE;
                dispose();
                break;
        }
    }
    
    public boolean selectionLoad() {
        Canvas canvas = Canvas.getInstance();
        Logger.log(
            "Loading initial project",
            Logger.LOG_TYPE.INFO
        );

        try {
            switch (this.setupcase) {
                case LOAD:
                    canvas.cleanCanvas();
                    try {
                        canvas.loadCanvas(ph.getCurrent().getCanvas());
                        return true;
                    } catch (java.io.IOException err) {
                        return false;
                    } catch (Exception err) {
                        Sentry.captureException(err);
                        Logger.log(
                            String.format(
                                "Running with an uncaught Exception: %s\n Stack trace: ",
                                err.getMessage()
                            ),
                            Logger.LOG_TYPE.ERROR,
                            Logger.COLOUR_SET.WARNING
                        );
                        err.printStackTrace();
                        return false;
                    }
                case CREATE:
                    canvas.cleanCanvas();
                    canvas.newCanvas(ph.getCurrent().dimension);
                    return true;
                default:
                    Sentry.captureMessage("No setup case was selected");
                    Logger.log(
                        "No setup case was selected",
                        Logger.LOG_TYPE.ERROR
                    );
                    throw new RuntimeException("No setup case was selected");
            }
        } catch (InUseCanvasException err) {
            Sentry.captureException(err);
            Logger.log(
                "The Canvas was not free before loading data",
                Logger.LOG_TYPE.ERROR
            );
            throw new RuntimeException(err);
        } 
    }

    public boolean getValue() {
        return !sentryToggle.isSelected();
    }
}
