package tools;

import javax.swing.*;
import java.awt.*;

public class Misc {
    public static void errorBox(Component c, String msg, String title) {
        JOptionPane.showMessageDialog(c, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean isHexChar(char c1)
    {
        return ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f'));
    }

    public static long readInputBox(JTextField tf, long oldvalue) throws NumberFormatException {
        int state = 0;
        long res = 0;
        String txt = tf.getText().trim();
        if (txt.charAt(0) == '-') {
            state = -1;
            txt = txt.substring(1);
        } else if (txt.charAt(0) == '+') {
            state = 1;
            txt = txt.substring(1);
        }
        if (txt.startsWith("0x"))
            res = Long.parseLong(txt.substring(2), 16);
        else
            res = Integer.parseInt(txt, 10);
        if (oldvalue == -1)
            return res;
        if (state == -1)
            return oldvalue - res;
        if (state == 1)
            return oldvalue + res;
        return res;
    }
}
