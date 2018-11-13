package tools;

import javax.swing.*;

public class Misc
{
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
