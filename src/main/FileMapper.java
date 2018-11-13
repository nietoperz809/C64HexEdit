package main;

import c64terminal.C64Character;
import c64terminal.C64Color;
import c64terminal.C64VideoMatrix;
import c64terminal.CharacterWriter;
import sun.nio.ch.DirectBuffer;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.String.format;

public class FileMapper
{
    private RandomAccessFile raFile;
    private long filesize = 0;
    private final C64VideoMatrix matrix;
    private final byte[] mappedBytes = new byte[C64VideoMatrix.LINES_ON_SCREEN * 8];
    private File inputFile;
    private JScrollBar scroller;

    public FileMapper (File f, C64VideoMatrix matrix, JScrollBar scroller) throws Exception
    {
        this.matrix = matrix;
        this.inputFile = f;
        this.scroller = scroller;
        filesize = f.length();
        raFile = new RandomAccessFile(inputFile, "rws");
    }

    public void close()
    {
        try
        {
            setFileSize (filesize, false);
            raFile.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void unmap (MappedByteBuffer buffer)
    {
        sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
        cleaner.clean();
    }

    /**
     * Write bytes into file
     * @param dat bytes to write
     * @param offs position of file where writing begins
     * @throws Exception if something went wrong
     */
    public void putBytes (byte[] dat, long offs) throws Exception
    {
        long total = offs+dat.length;
        if (total > filesize)
            filesize = total;
        FileChannel chan = raFile.getChannel();
        MappedByteBuffer buff = chan.map(FileChannel.MapMode.READ_WRITE, offs, dat.length);
        buff.put(dat);
        buff.force();
        //offset = offs;
        unmap (buff);
        displayMap();
    }

    /**
     * Read bytes from file
     * @param offs Position where to begin reading
     * @param dest buffer to receive the bytes
     * @throws Exception if something went wrong
     */
    public void getBytes (long offs, byte[] dest) throws Exception
    {
        FileChannel chan = raFile.getChannel();
        MappedByteBuffer buff = chan.map(FileChannel.MapMode.READ_ONLY, offs, dest.length);
        buff.get(dest);
        unmap (buff);
    }


    /**
     * Draws mapped bytes into C64 display
     * @throws Exception if something went wrong
     */
    public void displayMap () throws Exception
    {
        getBytes(scroller.getValue(), mappedBytes);
        for (int s = 0; s < C64VideoMatrix.LINES_ON_SCREEN; s++)
        {
            C64Character[] c64 = matrix.get(s);
            String addr = format("%08x", s * 8 + scroller.getValue());
            CharacterWriter cw = CharacterWriter.getInstance();
            for (int t = 0; t < 8; t++)
            {
                cw.writeChar(c64[t], addr.charAt(t), C64Color.YELLOW);
                int idx = 3 * t + 9;
                int src_idx = t + s * 8;
                String temp = String.format("%02x", mappedBytes[src_idx]);
                C64Color col = isPosFileEnd(t, s) ? C64Color.BLACK : C64Color.WHITE;
                cw.writeChar(c64[idx], temp.charAt(0), col);
                cw.writeChar(c64[idx + 1], temp.charAt(1), col);
                idx = 33 + t;
                cw.writeChar(c64[idx], (char) mappedBytes[src_idx], C64Color.ORANGE);
            }
        }
    }

    /**
     * Detect if current position is end of file
     * @param t Inner loop counter
     * @param s Outer loop counter
     * @return true or false
     * @throws Exception if something went wrong
     */
    private boolean isPosFileEnd (int t, int s) throws Exception
    {
        long pos = scroller.getValue() + s * 8 + t;
        return pos == filesize;
    }

    /**
     * Cursor hits bottom
     */
    public void scrollUp ()
    {
        scroller.setValue(scroller.getValue()+8);
        //offset += 8;
        try
        {
            displayMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Cursor hits top
     */
    public void scrollDown ()
    {
        scroller.setValue(scroller.getValue()-8);
//        offset -= 8;
//        if (offset < 0)
//            offset = 0;
        try
        {
            displayMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Scrollbar is moved
     * @param value Scrollbar value
     */
    public void setScrollbarOffset (long value)
    {
        //offset = value;
        try
        {
            displayMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setFileSize (long size, boolean display)
    {
        FileChannel chan = raFile.getChannel();
        {
            try
            {
                chan.truncate(size);
                filesize = size;
                if (display)
                    displayMap();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

//    public void adjustFileSize ()
//    {
//        setFileSize (filesize, false);
//    }
}
