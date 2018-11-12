package dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

import static tools.Misc.readInputBox;

public class FeatureDlg extends JDialog
{
	private final JPanel contentPanel = new JPanel();
	private JTextField fromField;
	private JTextField sizeField;
	private JTextField patternField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
    private byte[] data;

    private void allocDataField()
    {
        int n = 0;
        try
        {
            data = new byte[(int)readInputBox(sizeField)];
        }
        catch (Exception e)
        {
            data = new byte[256];
        }
    }

    static public class DlgRes
    {
        public byte[] data;
        public long startPos;
    }

    public static DlgRes startDlg()
    {
        FeatureDlg dialog = new FeatureDlg();
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        DlgRes res = new DlgRes();
        res.data = dialog.data;
        try
        {
            res.startPos = readInputBox(dialog.fromField);
        }
        catch (Exception e)
        {
            res.startPos = 0;
        }
        return res;
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FeatureDlg dialog = new FeatureDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FeatureDlg() {
		setTitle("Fill with");
		setBounds(100, 100, 346, 387);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		fromField = new JTextField();
		fromField.setBounds(170, 28, 116, 22);
		contentPanel.add(fromField);
		fromField.setColumns(10);

		sizeField = new JTextField();
		sizeField.setBounds(170, 63, 116, 22);
		contentPanel.add(sizeField);
		sizeField.setColumns(10);

		JLabel lblFrom = new JLabel("From");
		lblFrom.setBounds(137, 31, 43, 16);
		contentPanel.add(lblFrom);

		JLabel lblSize = new JLabel("Size");
		lblSize.setBounds(140, 66, 24, 16);
		contentPanel.add(lblSize);

		JRadioButton rdbtnCountUp = new JRadioButton("Count up");
		rdbtnCountUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
			    allocDataField();
			    for (int s=0; s<data.length; s++)
			        data[s] = (byte)s;
			}
		});
		buttonGroup.add(rdbtnCountUp);
		rdbtnCountUp.setBounds(26, 28, 91, 25);
		contentPanel.add(rdbtnCountUp);

		JRadioButton rdbtnCountDown = new JRadioButton("Count down");
		rdbtnCountDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                for (int s=0; s<data.length; s++)
                    data[s] = (byte)(s-256);
            }
		});
		buttonGroup.add(rdbtnCountDown);
		rdbtnCountDown.setBounds(26, 58, 116, 25);
		contentPanel.add(rdbtnCountDown);

		JRadioButton rdbtnZeros = new JRadioButton("Zeros");
		rdbtnZeros.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                for (int s=0; s<data.length; s++)
                    data[s] = 0;
            }
		});
		buttonGroup.add(rdbtnZeros);
		rdbtnZeros.setBounds(26, 88, 91, 25);
		contentPanel.add(rdbtnZeros);

		JRadioButton rdbtnFfs = new JRadioButton("FF's");
		rdbtnFfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                for (int s=0; s<data.length; s++)
                    data[s] = (byte)0xff;
            }
		});
		buttonGroup.add(rdbtnFfs);
		rdbtnFfs.setBounds(26, 118, 127, 25);
		contentPanel.add(rdbtnFfs);

		JRadioButton rdbtnaa = new JRadioButton("55AA");
		rdbtnaa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                for (int s=0; s<data.length; s++)
                {
                    data[s] = (byte) (s%2 == 0 ? 0x55 : 0xaa);
                }
            }
		});
		buttonGroup.add(rdbtnaa);
		rdbtnaa.setBounds(26, 148, 101, 25);
		contentPanel.add(rdbtnaa);

		JRadioButton rdbtnAa = new JRadioButton("AA55");
		rdbtnAa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                for (int s=0; s<data.length; s++)
                {
                    data[s] = (byte) (s%2 == 1 ? 0x55 : 0xaa);
                }
            }
		});
		buttonGroup.add(rdbtnAa);
		rdbtnAa.setBounds(26, 178, 127, 25);
		contentPanel.add(rdbtnAa);

		JRadioButton rdbtnRandom = new JRadioButton("Random");
		rdbtnRandom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                allocDataField();
                SecureRandom random = new SecureRandom();
                random.nextBytes(data);
            }
		});
		buttonGroup.add(rdbtnRandom);
		rdbtnRandom.setBounds(26, 208, 82, 25);
		contentPanel.add(rdbtnRandom);

		JRadioButton rdbtnText = new JRadioButton("Text");
		rdbtnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
                allocDataField();
                byte[] pat = patternField.getText().getBytes();
                for (int s=0; s<data.length; s++)
                {
                    data[s] = pat[s%pat.length];
                }
			}
		});
		buttonGroup.add(rdbtnText);
		rdbtnText.setBounds(26, 238, 67, 25);
		contentPanel.add(rdbtnText);

		patternField = new JTextField();
		patternField.setBounds(137, 147, 149, 22);
		contentPanel.add(patternField);
		patternField.setColumns(10);

		JRadioButton rdbtnPattern = new JRadioButton("Hex Pattern");
		rdbtnPattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
                allocDataField();
                byte[] hex = DatatypeConverter.parseHexBinary(patternField.getText());
                for (int s=0; s<data.length; s++)
                {
                    data[s] = hex[s%hex.length];
                }
            }
		});
		buttonGroup.add(rdbtnPattern);
		rdbtnPattern.setBounds(26, 268, 91, 25);
		contentPanel.add(rdbtnPattern);

		JLabel lblTextOrPattern = new JLabel("Text or Pattern");
		lblTextOrPattern.setBounds(170, 118, 101, 16);
		contentPanel.add(lblTextOrPattern);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(e ->
                {
                    this.dispose();
                });
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
                cancelButton.addActionListener(e ->
                {
                    data = null;
                    this.dispose();
                });
			}
		}
	}
}
