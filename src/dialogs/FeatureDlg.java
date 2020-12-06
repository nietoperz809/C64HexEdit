package dialogs;

import main.FileMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;

import static tools.Misc.readInputBox;


class MyRadio extends JRadioButton {
    private static int counter;

    static void setCounter(int num) {
        counter = num;
    }

    MyRadio(String txt) {
        super(txt);
        setActionCommand("" + counter);
        counter++;
    }
}

public class FeatureDlg extends JDialog {
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final JTextField textField_3;
    private final JTextField labFile;
    private final JTextField fromField;
    private final JTextField sizeField;
    private final JTextField patternField;
    private byte[] data;
    private final FileMapper mapper;

    /**
     * Create the dialog.
     */
    public FeatureDlg(FileMapper mapper) {
        this.mapper = mapper;
        MyRadio.setCounter(1);
        setTitle("Fill with");
        setBounds(100, 100, 346, 465);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        fromField = new JTextField("0");
        fromField.setBounds(170, 28, 116, 22);
        contentPanel.add(fromField);
        fromField.setColumns(10);

        sizeField = new JTextField("8");
        sizeField.setBounds(170, 63, 116, 22);
        contentPanel.add(sizeField);
        sizeField.setColumns(10);

        JLabel lblFrom = new JLabel("Address");
        lblFrom.setBounds(120, 31, 50, 16);
        contentPanel.add(lblFrom);

        JLabel lblSize = new JLabel("Size");
        lblSize.setBounds(140, 66, 24, 16);
        contentPanel.add(lblSize);

        JRadioButton rdbtnCountUp = new MyRadio("Count up");
        buttonGroup.add(rdbtnCountUp);
        rdbtnCountUp.setBounds(26, 28, 91, 25);
        contentPanel.add(rdbtnCountUp);

        JRadioButton rdbtnCountDown = new MyRadio("Count down");
        buttonGroup.add(rdbtnCountDown);
        rdbtnCountDown.setBounds(26, 48, 116, 25);
        contentPanel.add(rdbtnCountDown);

        JRadioButton rdbtnZeros = new MyRadio("Zeros");
        buttonGroup.add(rdbtnZeros);
        rdbtnZeros.setBounds(26, 68, 91, 25);
        contentPanel.add(rdbtnZeros);

        JRadioButton rdbtnFfs = new MyRadio("FF's");
        buttonGroup.add(rdbtnFfs);
        rdbtnFfs.setBounds(26, 88, 100, 25);
        contentPanel.add(rdbtnFfs);

        JRadioButton rdbtnaa = new MyRadio("55AA");
        buttonGroup.add(rdbtnaa);
        rdbtnaa.setBounds(26, 108, 100, 25);
        contentPanel.add(rdbtnaa);

        JRadioButton rdbtnAa = new MyRadio("AA55");
        buttonGroup.add(rdbtnAa);
        rdbtnAa.setBounds(26, 128, 100, 25);
        contentPanel.add(rdbtnAa);

        JRadioButton rdbtnRandom = new MyRadio("Random");
        buttonGroup.add(rdbtnRandom);
        rdbtnRandom.setBounds(26, 148, 82, 25);
        contentPanel.add(rdbtnRandom);

        JRadioButton rdbtnText = new MyRadio("Text");
        buttonGroup.add(rdbtnText);
        rdbtnText.setBounds(26, 168, 67, 25);
        contentPanel.add(rdbtnText);

        JRadioButton rdbtnPattern = new MyRadio("Hex Pattern");
        buttonGroup.add(rdbtnPattern);
        rdbtnPattern.setBounds(26, 188, 91, 25);
        contentPanel.add(rdbtnPattern);

        JRadioButton rdbtnXorWpattern = new MyRadio("Xor FF");
        buttonGroup.add(rdbtnXorWpattern);
        rdbtnXorWpattern.setBounds(26, 208, 127, 25);
        contentPanel.add(rdbtnXorWpattern);

        labFile = new JTextField();
        labFile.setBounds(20, 336, 280, 25);
        labFile.setEditable(false);
        contentPanel.add(labFile);

        JRadioButton rbFile = new MyRadio("File Data");
        buttonGroup.add(rbFile);
        rbFile.setBounds(26, 228, 100, 25);
        contentPanel.add(rbFile);
        rbFile.addActionListener(e ->
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(FeatureDlg.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                labFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JLabel lblTextOrPattern = new JLabel("Text or Pattern");
        lblTextOrPattern.setBounds(170, 125, 101, 16);
        contentPanel.add(lblTextOrPattern);

        patternField = new JTextField();
        patternField.setBounds(137, 147, 149, 22);
        contentPanel.add(patternField);
        patternField.setColumns(10);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        textField_3 = new JTextField();
        textField_3.setBounds(155, 239, 116, 22);
        contentPanel.add(textField_3);
        textField_3.setColumns(10);

        JButton btnNewButton = new JButton("New Size");
        btnNewButton.addActionListener(e ->
        {
            mapper.setFileSize(readInputBox(textField_3, mapper.getFilesize()));
            mapper.displayMap();
            //this.dispose();
        });
        btnNewButton.setBounds(165, 268, 97, 25);
        contentPanel.add(btnNewButton);

        JButton okButton = new JButton("OK");
        buttonPane.add(okButton);
        okButton.addActionListener(e ->
        {
            handleRadioButton();
            this.dispose();
        });

        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e ->
        {
            data = null;
            this.dispose();
        });
    }

    public static DlgRes startDlg(FileMapper mapper) {
        FeatureDlg dialog = new FeatureDlg(mapper);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        DlgRes res = new DlgRes();
        res.data = dialog.data;
        try {
            res.startPos = readInputBox(dialog.fromField, -1);
        } catch (Exception e) {
            res.startPos = 0;
        }
        return res;
    }

    private void handleRadioButton() {
        ButtonModel mod = buttonGroup.getSelection();
        if (mod == null)
            return;
        allocDataField();
        String act = mod.getActionCommand();
        switch (act) {
            case "1":
                for (int s = 0; s < data.length; s++) {
                    data[s] = (byte) s;
                }
                break;

            case "2":
                for (int s = 0; s < data.length; s++) {
                    data[s] = (byte) (255 - s);
                }
                break;

            case "3":
                Arrays.fill(data, (byte) 0);
                break;

            case "4":
                Arrays.fill(data, (byte) 0xff);
                break;

            case "5":
                for (int s = 0; s < data.length; s++) {
                    data[s] = (byte) (s % 2 == 0 ? 0x55 : 0xaa);
                }
                break;

            case "6":
                for (int s = 0; s < data.length; s++) {
                    data[s] = (byte) (s % 2 == 1 ? 0x55 : 0xaa);
                }
                break;

            case "7":
                SecureRandom random = new SecureRandom();
                random.nextBytes(data);
                break;

            case "8":
                byte[] pat = patternField.getText().getBytes();
                for (int s = 0; s < data.length; s++) {
                    data[s] = pat[s % pat.length];
                }
                break;

            case "9":
                byte[] hex = DatatypeConverter.parseHexBinary(patternField.getText());
                for (int s = 0; s < data.length; s++) {
                    data[s] = hex[s % hex.length];
                }
                break;

            case "10":
                try {
                    long from = readInputBox(fromField, -1);
                    byte[] orig = new byte[data.length];
                    mapper.getBytes(from, orig);
                    for (int s = 0; s < data.length; s++) {
                        data[s] = (byte) (orig[s] ^ 0xff);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "11": {
                try {
                    data = Files.readAllBytes(Paths.get(labFile.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private void allocDataField() {
        try {
            data = new byte[(int) readInputBox(sizeField, -1)];
        } catch (Exception e) {
            data = new byte[1];
        }
    }

    static public class DlgRes {
        public byte[] data;
        public long startPos;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            FeatureDlg dialog = new FeatureDlg(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
