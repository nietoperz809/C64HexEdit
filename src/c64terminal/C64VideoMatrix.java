package c64terminal;

import main.FileMapper;
import tools.ConstantRing;

import java.awt.*;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Created
 */
public class C64VideoMatrix extends ArrayList<C64Character[]> {
    public static final int LINES_ON_SCREEN = 25;
    public static final int CHARS_PER_LINE = 41;
    private static final int NO_CHARACTER = ' ';
    static final int SCALE = 16;
    private final C64Panel pane;
    public ConstantRing valid_xpos =
            new ConstantRing(new int[]{9, 10, 12, 13, 15, 16, 18, 19, 21, 22, 24, 25, 27, 28, 30, 31});

    private FileMapper mapper;

    private final Point currentCursorPos = new Point(0, 0);
    /**
     * Color used for new chars on screen
     */
    private byte defaultColorIndex = 3;

    public void setMapper(FileMapper f) {
        mapper = f;
    }

    public FileMapper getMapper() {
        return mapper;
    }

    public C64VideoMatrix(C64Panel pane) {
        this.pane = pane;
        clearScreen();
    }

    /**
     * Fill screen with blanks
     */
    synchronized public void clearScreen() {
        this.clear();
        for (int s = 0; s < LINES_ON_SCREEN + 1; s++) {
            add(createEmptyLine());
        }
        currentCursorPos.x = valid_xpos.getFirst();
        currentCursorPos.y = 0;
    }

    synchronized public void setCursorPos(int x, int y) {
        currentCursorPos.x = valid_xpos.nearest(x);
        currentCursorPos.y = y;
    }

    /**
     * Make a line of blanks
     *
     * @return The line
     */
    private C64Character[] createEmptyLine() {
        C64Character[] c = new C64Character[CHARS_PER_LINE];
        for (int s = 0; s < CHARS_PER_LINE; s++) {
            c[s] = new C64Character(NO_CHARACTER, defaultColorIndex);
        }
        return c;
    }

    public boolean isValidKey(char c, int keyCode, boolean action) {
        if (action || c == '\uFFFF') {
            return false;
        }
        if (keyCode == VK_ENTER) {
            return false;
        }
        return true;
    }

    /**
     * Injects new Char
     *
     * @param c       the character
     */
    synchronized public void putChar(char c) {
        C64Character[] line = get(currentCursorPos.y);
        line[currentCursorPos.x].face = c;
        line[currentCursorPos.x].colorIndex = defaultColorIndex;
        if (currentCursorPos.x < (CHARS_PER_LINE - 1))
            currentCursorPos.x = valid_xpos.next(currentCursorPos.x);
    }

    /**
     * Gets input line as char array only
     * input can be concatenation of multiple lines
     *
     * @return a char array
     */
    synchronized public char[] readLine() {
        ArrayList<Character> arr = new ArrayList<>();
        C64Character c64[] = get(currentCursorPos.y);
        return CharacterWriter.getInstance().mapCBMtoPC(c64);
    }

    /**
     * Move cursor up
     */
    synchronized public void up() {
        if (currentCursorPos.y > 0) {
            currentCursorPos.y--;
        } else {
            if (mapper != null)
                mapper.scrollDown();
        }
        pane.repaint();
    }

    /**
     * Move cursor down
     */
    synchronized public void down() {
        if (currentCursorPos.y < LINES_ON_SCREEN - 1) {
            currentCursorPos.y++;
        } else {
            if (mapper != null)
                mapper.scrollUp();
        }
        pane.repaint();
    }

    /**
     * Move cursor left
     */
    synchronized public void left() {
        if (currentCursorPos.x > 0)
            currentCursorPos.x = valid_xpos.prev(currentCursorPos.x);
        pane.repaint();
    }


    /**
     * Move cursor right
     */
    synchronized public void right() {
        if (currentCursorPos.x < CHARS_PER_LINE - 1)
            currentCursorPos.x = valid_xpos.next(currentCursorPos.x);
        pane.repaint();
    }


    private boolean blinkflag = false;

    public void render(Graphics g) {
        CharacterWriter writer = CharacterWriter.getInstance();
        int ypos = 0;
        for (int y = 0; y < LINES_ON_SCREEN; y++) {
            int xpos = 0;
            for (int x = 0; x < CHARS_PER_LINE; x++) {
                C64Character c64c = get(y)[x];
                int face = c64c.face & 0x00ff;
                g.setColor(C64Color.values()[c64c.colorIndex].getColor());
                g.fillRect(xpos, ypos, SCALE, SCALE);
                g.drawImage(writer.imageMap.get((char) face),
                        xpos, ypos, SCALE, SCALE, null);
                xpos += SCALE;
            }
            ypos += SCALE;
        }
        if (blinkflag) {
            g.setColor(Color.GREEN);
            g.fillRect(currentCursorPos.x * SCALE,
                    currentCursorPos.y * SCALE, SCALE, SCALE);
        }
        blinkflag = !blinkflag;
    }
}
