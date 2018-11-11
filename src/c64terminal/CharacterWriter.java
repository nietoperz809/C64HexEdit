package c64terminal;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Singleton class
 */
public class CharacterWriter implements CharacterROM
{
    private static final Color TRANSPARENT = new Color (1,2,3);
    private static CharacterWriter instance = null;
    final HashMap<Character, Image> imageMap = new HashMap<>();
    private final HashMap<Character, Character> keyMap = new HashMap<>();
    private final HashMap<Character, Character> reverseKeyMap = new HashMap<>();
    private int backgroundColor = C64Colors.BLUE.getRGB();
    private boolean shifted = true;
    /**
     * Constructor, fills the char imageMap
     */
    private CharacterWriter ()
    {
        fillImageMap();

        for (char s = 'a'; s <= 'z'; s++)
        {
            char t = (char) (s - 'a' + 1);
            setMaps(s, t);
        }
        setMaps('@', (char) 0);

        setMaps ('^', (char)30);
    }

    private void setMaps (char a, char b)
    {
        keyMap.put(a, b);
        reverseKeyMap.put(b, a);
    }

    private void fillImageMap ()
    {
        imageMap.clear();
        for (int s = 0; s < 256; s++)
        {
            int idx = shifted ? s*8 : (s+256) * 8;
            imageMap.put((char) s, getImage(idx));
        }
    }

    private Image getImage (int idx)
    {
        BufferedImage img = new BufferedImage(8, 8, TYPE_INT_ARGB);
        for (int rows = 0; rows < 8; rows++)
        {
            int c = characterData[idx++];
            int i = 128;
            for (int lines = 0; lines < 8; lines++)
            {
                if ((c & i) == i)
                {
                    img.setRGB(lines, rows, TRANSPARENT.getRGB());
                }
                else
                {
                    img.setRGB(lines, rows, backgroundColor);
                }
                i >>>= 1;
            }
        }
        return BitmapTools.makeColorTransparent(img, TRANSPARENT);
    }

    public static CharacterWriter getInstance ()
    {
        if (instance == null)
        {
            instance = new CharacterWriter();
        }
        return instance;
    }

// --Commented out by Inspection START (11/11/2018 8:53 PM):
//    void setBackgroundColor (int idx)
//    {
// --Commented out by Inspection START (11/11/2018 8:53 PM):
////        backgroundColor = C64Colors.values()[idx].getRGB();
////        fillImageMap();
////    }
//// --Commented out by Inspection STOP (11/11/2018 8:53 PM)
// --Commented out by Inspection STOP (11/11/2018 8:53 PM)

    void switchCharset()
    {
        shifted = !shifted;
        fillImageMap();
    }

// --Commented out by Inspection START (11/11/2018 8:53 PM):
//    public char[] mapCBMtoPC (Character[] in)
//    {
//        char[] out = new char[in.length];
//        for (int s = 0; s < in.length; s++)
//        {
//            Character c1 = reverseKeyMap.get(in[s]);
//            out[s] = c1 == null ? in[s] : c1;
//        }
//        return out;
//    }
// --Commented out by Inspection STOP (11/11/2018 8:53 PM)

    public char[] mapCBMtoPC (C64Character[] in)
    {
        char[] out = new char[in.length];
        for (int s = 0; s < in.length; s++)
        {
            Character c1 = reverseKeyMap.get((char)in[s].face);
            out[s] = c1 == null ? (char)in[s].face : c1;
        }
        return out;
    }

    public char mapPCtoCBM (char in)
    {
        Character c1 = keyMap.get(in);
        return c1 == null ? in : c1;
    }

    public void writeChar (C64Character c, char face, C64Colors col)
    {
        c.colorIndex = col.ordinal();
        c.face = mapPCtoCBM(face);
    }

}
