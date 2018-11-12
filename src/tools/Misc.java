package tools;

import javax.swing.*;

public class Misc
{
    public static long readInputBox(JTextField tf) throws Exception
    {
        String txt = tf.getText();
        if (txt.startsWith("0x"))
        {
            return Long.parseLong(txt.substring(2), 16);
        }
        return Long.parseLong(txt, 10);
    }

}
