package c64terminal;

import main.FileMapper;
import tools.ConstantRing;

import java.awt.*;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Created
 */
public class C64VideoMatrix extends ArrayList<C64Character[]>
{
    public static final int LINES_ON_SCREEN = 25;
    public static final int CHARS_PER_LINE = 41;
    private static final int NO_CHARACTER = ' ';
    static final int SCALE=16;
    private final C64Panel pane;
    public ConstantRing valid_xpos =
            new ConstantRing(new int[]{9,10,12,13,15,16,18,19,21,22,24,25,27,28,30,31});

    private FileMapper mapper;

    private final Point currentCursorPos = new Point(0,0);
    /**
     * Color used for new chars on screen
     */
    private byte defaultColorIndex = 3;

    public void setMapper (FileMapper f)
    {
        mapper = f;
    }

    public FileMapper getMapper ()
    {
        return mapper;
    }

    public C64VideoMatrix (C64Panel pane)
    {
        this.pane = pane;
        clearScreen();
    }

    /**
     * Fill screen with blanks
     */
    synchronized public void clearScreen ()
    {
        this.clear();
        for (int s = 0; s< LINES_ON_SCREEN+1; s++)
        {
            add(createEmptyLine());
        }
        currentCursorPos.x = valid_xpos.getFirst();
        currentCursorPos.y = 0;
    }

    synchronized public void setCursorPos (int x, int y)
    {
        currentCursorPos.x = valid_xpos.nearest(x);
        currentCursorPos.y = y;
    }

    /**
     * Make a line of blanks
     * @return The line
     */
    private C64Character[] createEmptyLine ()
    {
        C64Character[] c = new C64Character[CHARS_PER_LINE];
        for (int s = 0; s< CHARS_PER_LINE; s++)
        {
            c[s] = new C64Character(NO_CHARACTER, defaultColorIndex);
        }
        return c;
    }

//    /**
//     * Move cursor to the line below
//     * Shifts lines up and creates a new one at the end
//     * if cursor moves behind last line.
//     */
//    private synchronized void nextLine ()
//    {
////        currentCursorPos.x = 0;
////        currentCursorPos.y++;
////        if (currentCursorPos.y == LINES_ON_SCREEN)
////        {
////            currentCursorPos.y--;
////            for (int s = 0; s< LINES_ON_SCREEN -1; s++)
////            {
////                set (s, get(s+1));
////            }
////            set (LINES_ON_SCREEN -1, createEmptyLine());
////        }
//    }

    /**
     * Injects new Char
     * @param c the character
     * @param keyCode keyCode from KeyEvent
     * @param action  action from KeyEvent
     */
    synchronized public void putChar (char c, int keyCode, boolean action)
    {
        if (action || c == '\uFFFF')
        {
            return;
        }
        if (keyCode == VK_ENTER)
        {
        }
        else
        {
            C64Character[] line = get(currentCursorPos.y);
            line[currentCursorPos.x].face = c;
            line[currentCursorPos.x].colorIndex = defaultColorIndex;
            if (currentCursorPos.x < (CHARS_PER_LINE-1))
                currentCursorPos.x = valid_xpos.next(currentCursorPos.x);
        }
    }

//    /**
//     * Get current cursor position
//     * @return the cursorPos (cloned)
//     */
//    synchronized public Point getCursor()
//    {
//        return (Point) currentCursorPos.clone();
//    }

    /**
     * Gets input line as char array only
     * input can be concatenation of multiple lines
     *
     * @return a char array
     */
    synchronized public char[] readLine ()
    {
        ArrayList<Character> arr = new ArrayList<>();
        C64Character c64[] = get(currentCursorPos.y);
        return CharacterWriter.getInstance().mapCBMtoPC(c64);
    }

    /**
     * Move cursor up
     */
    synchronized public void up()
    {
        if (currentCursorPos.y > 0)
        {
            currentCursorPos.y--;
        }
        else
        {
            if (mapper != null)
                mapper.scrollDown();
        }
        pane.repaint();
    }

    /**
     * Move cursor down
     */
    synchronized public void down()
    {
        if (currentCursorPos.y < LINES_ON_SCREEN-1)
        {
            currentCursorPos.y++;
        }
        else
        {
            if (mapper != null)
                mapper.scrollUp();
        }
        pane.repaint();
    }

    /**
     * Move cursor left
     */
    synchronized public void left()
    {
        if (currentCursorPos.x > 0)
            currentCursorPos.x = valid_xpos.prev(currentCursorPos.x);
        pane.repaint();
    }


    /**
     * Move cursor right
     */
    synchronized public void right()
    {
        if (currentCursorPos.x < CHARS_PER_LINE-1)
            currentCursorPos.x = valid_xpos.next(currentCursorPos.x);
        pane.repaint();
    }

//    /**
//     * Insert a backspace
//     */
//    synchronized public void backspace()
//    {
//        left();
//        putChar(' ', 0, false);
//        left();
//    }

//    /**
//     * Inserts entire string
//     * @param str
//     */
//    synchronized void putString (String str)
//    {
//        str = str.toLowerCase();
//        for (int s=0; s<str.length(); s++)
//        {
//            char c = str.charAt(s);
//            int keycode = c == '\n' ? VK_ENTER : 0;
//            c = CharacterWriter.getInstance().mapPCtoCBM(c);
//            putChar(c, keycode, false);
//        }
//    }


//    /**
//     * Convert screen memory address to coordinates
//     * @param addr memory address, must be >= 1024 and <= 1024+25*40
//     * @return a point givin the x/y coordinates
//     */
//    private Point elementfromAddress (int addr) throws Exception
//    {
//        Point p = new Point (addr % CHARS_PER_LINE, addr / CHARS_PER_LINE);
//        if (p.y >= LINES_ON_SCREEN+1)
//            throw new Exception("Wrong screen address");
//        return p;
//    }

//    /**
//     * Get value at specified address
//     * @param offset screen memory address
//     * @return the value
//     */
//    synchronized public int peekFace (int offset) throws Exception
//    {
//        Point p = elementfromAddress(offset);
//        return get(p.y)[p.x].face;
//    }

//    synchronized public int peekColor (int offset) throws Exception
//    {
//        Point p = elementfromAddress(offset);
//        return get(p.y)[p.x].colorIndex;
//    }
//
//    /**
//     * Set value at specified address
//     * @param offset screen memory address
//     * @param val the new value
//     */
//    synchronized public void pokeFace (int offset, int val) throws Exception
//    {
//        Point p = elementfromAddress(offset);
//        get(p.y)[p.x].face = val;
//    }
//
//    synchronized public void pokeColor (int offset, int val) throws Exception
//    {
//        Point p = elementfromAddress(offset);
//        get(p.y)[p.x].colorIndex = val;
//    }

    private boolean blinkflag = false;
    public void render (Graphics g)
    {
        CharacterWriter writer = CharacterWriter.getInstance();
        int ypos = 0;
        for (int y = 0; y<LINES_ON_SCREEN; y++)
        {
            int xpos = 0;
            for (int x = 0; x<CHARS_PER_LINE; x++)
            {
                C64Character c64c = get(y)[x];
                int face = c64c.face & 0x00ff;
                g.setColor (C64Color.values()[c64c.colorIndex].getColor());
                g.fillRect(xpos, ypos, SCALE, SCALE);
                g.drawImage(writer.imageMap.get((char)face),
                        xpos, ypos, SCALE, SCALE, null);
                xpos += SCALE;
            }
            ypos += SCALE;
        }
        if (blinkflag)
        {
            g.setColor(Color.GREEN);
            g.fillRect(currentCursorPos.x * SCALE,
                    currentCursorPos.y * SCALE, SCALE, SCALE);
        }
        blinkflag = !blinkflag;
    }
}
