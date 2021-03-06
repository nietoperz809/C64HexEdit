package main;

import c64terminal.C64VideoMatrix;
import dialogs.FeatureDlg;
import tools.Misc;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static tools.Misc.readInputBox;

public class ActionPanel {
    private JPanel thisPanel;
    private JButton openButton;
    private JLabel fileName;
    private FileMapper mapper;
    private C64VideoMatrix matrix;
    private JTextField addressInput;
    private JButton changeButton;
    private JScrollBar scroller;

    public FileMapper getMapper() {
        return mapper;
    }

    public void fileOpen() {
        openButton.doClick();
    }

    public ActionPanel(final C64VideoMatrix mat, JScrollBar scr) {
        setupUI();
        matrix = mat;
        scroller = scr;

        addressInput.addActionListener(e -> {
            try {
                scroller.setValue((int) readInputBox(addressInput, -1));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(thisPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileName.setText(selectedFile.getAbsolutePath() + " : " +
                        String.format("%x", selectedFile.length()));
                try {
                    if (mapper != null)
                        mapper.close();
                    mapper = new FileMapper(selectedFile, matrix, scroller);
                    if (mapper.isFakeFile()) {
                        Misc.errorBox(thisPanel, "using Fake File", "File not accessible");
                    }
                    matrix.setMapper(mapper);
                    mapper.displayMap();
                    changeButton.setEnabled(true);
                    addressInput.setEnabled(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                Misc.errorBox(thisPanel, "File required", "App will close now");
                System.exit(1);
            }
        });
    }

    private void setupUI() {
        thisPanel = new JPanel();
        thisPanel.setLayout(new BorderLayout(0, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        thisPanel.add(panel2, BorderLayout.WEST);
        openButton = new JButton("File");
        openButton.setToolTipText("Click to open existing or create a new file");
        panel2.add(openButton);
        addressInput = new JTextField("0x10000000");
        addressInput.setToolTipText("New position+ENTER");
        addressInput.setEnabled(false);
        panel2.add(addressInput);
        changeButton = new JButton("Change");
        changeButton.setToolTipText("Open file manipulation dialog");
        changeButton.setEnabled(false);
        changeButton.addActionListener(e -> {
            FeatureDlg.DlgRes res = FeatureDlg.startDlg(mapper);
            if (res.data == null)
                return;
            try {
                mapper.putBytes(res.data, res.startPos);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        panel2.add(changeButton);
        fileName = new JLabel();
        fileName.setHorizontalAlignment(0);
        fileName.setOpaque(true);
        fileName.setText("Label");
        thisPanel.add(fileName, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent getRootComponent() {
        return thisPanel;
    }
}
