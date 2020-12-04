package tools;

import javax.swing.*;
import java.awt.*;

public class Misc
{
    public static void errorBox(Component c, String msg, String title) {
        JOptionPane.showMessageDialog(c, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static long readInputBox(JTextField tf) throws NumberFormatException
    {
        String txt = tf.getText();
        if (txt.startsWith("0x"))
        {
            return Long.parseLong(txt.substring(2), 16);
        }
        return Integer.parseInt(txt, 10);
    }

}
