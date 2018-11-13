package c64terminal;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.glass.events.KeyEvent.VK_BACKSPACE;
import static java.awt.event.KeyEvent.VK_ENTER;

public class C64Panel extends JPanel
{
    private final static int SCALE=16;
    private final C64VideoMatrix matrix = new C64VideoMatrix();
    //private final RingBuffer<Character> ringBuff = new RingBuffer<>(40);

    public C64VideoMatrix getMatrix()
    {
        return matrix;
    }

    public C64Panel ()
    {
        setDoubleBuffered(true);
        setFocusable(true);
        setPreferredSize(new Dimension(
                C64VideoMatrix.CHARS_PER_LINE*SCALE,
                C64VideoMatrix.LINES_ON_SCREEN*SCALE));

        addMouseListener(new MouseInputAdapter()
        {
            @Override
            public void mouseClicked (MouseEvent e)
            {
                requestFocusInWindow();
                matrix.setCursorPos(e.getX()/C64VideoMatrix.SCALE,
                        e.getY()/C64VideoMatrix.SCALE);
            }
        });

        addKeyListener(new KeyAdapter()
        {
            void handleKey (KeyEvent e)
            {
                char c = e.getKeyChar();
                if (c == VK_ENTER)
                {
                    char[] arr = matrix.readLine();
                    long address = Long.parseLong(String.valueOf(arr, 0, 8), 16);
                    byte[] bt = new byte[8];
                    int start = 0;
                    for (int s=0; s<8; s++)
                    {
                        String s1 = "";
                        start = matrix.valid_xpos.next(start);
                        s1 = s1+arr[start];
                        start = matrix.valid_xpos.next(start);
                        s1 = s1+arr[start];
                        bt[s] = (byte)Integer.parseInt(s1, 16);
                    }
                    try
                    {
                        matrix.getMapper().putBytes(bt, address); //setBytes(address, bt);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
                else if (c != VK_BACKSPACE)
                {
                    matrix.putChar(CharacterWriter.getInstance().mapPCtoCBM(c),
                        e.getKeyCode(), e.isActionKey());
                }
//                ringBuff.add(c);
//                matrix.putChar(CharacterWriter.getInstance().mapPCtoCBM(c),
//                        e.getKeyCode(), e.isActionKey());
            }

            void handleSpecialKeys (KeyEvent e)
            {
                switch (e.getKeyCode())
                {
//                    case KeyEvent.VK_BACK_SPACE:
//                        matrix.backspace();
//                        break;

                    case KeyEvent.VK_LEFT:
                        matrix.left();
                        break;

                    case KeyEvent.VK_RIGHT:
                        matrix.right();
                        break;

                    case KeyEvent.VK_UP:
                        matrix.up();
                        break;

                    case KeyEvent.VK_DOWN:
                        matrix.down();
                        break;
                }
            }

            @Override
            public void keyPressed (KeyEvent e)
            {
                handleKey(e);
                handleSpecialKeys(e);
                repaint();
            }
        });

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::repaint,
                100,
                500,
                TimeUnit.MILLISECONDS);
    }

    @Override
    protected void paintComponent (Graphics g)
    {
        matrix.render(g);
    }

    @Override
    public void update (Graphics g)
    {
        //super.update(g);
    }
}
