package main;

import c64terminal.C64Character;
import c64terminal.C64Color;
import c64terminal.C64VideoMatrix;
import c64terminal.CharacterWriter;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.String.format;

public class FileMapper
{
    private static FileChannel fileChannel;
    private static RandomAccessFile raFile;
    private final C64VideoMatrix matrix;
    private final int mappedLength = C64VideoMatrix.LINES_ON_SCREEN * 8;
    private final byte[] mappedBytes = new byte[mappedLength];
    private MappedByteBuffer byteBuffer;
    private long offset = 0;
    private long filesize = 0;
    private File inputFile;

    public FileMapper (File f, C64VideoMatrix matrix) throws Exception
    {
        close();
        this.matrix = matrix;
        this.inputFile = f;
        filesize = f.length();
        open();
    }

    public static void unmap (MappedByteBuffer buffer)
    {
        sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
        cleaner.clean();
    }

    /**
     * Close file and channel
     * @throws Exception if something went wrong
     */
    public void close () throws Exception
    {
        if (fileChannel != null)
        {
            fileChannel.close();
        }
        if (raFile != null)
        {
            raFile.close();
        }
        //unmap (byteBuffer);
    }

    /**
     * Open file and channel
     * @throws Exception if something went wrong
     */
    public void open () throws Exception
    {
        raFile = new RandomAccessFile(inputFile, "rws");
        fileChannel = raFile.getChannel();
    }

    /**
     * Write bytes into file by using a separate FileChannel
     * @param dat bytes to write
     * @param offs position of file where writing begins
     * @throws Exception if something went wrong
     */
    public void putBytes (byte[] dat, long offs) throws Exception
    {
        RandomAccessFile ra = new RandomAccessFile(inputFile, "rws");
        FileChannel chan = ra.getChannel();
        MappedByteBuffer buff = chan.map(FileChannel.MapMode.READ_WRITE, offs, dat.length);
        buff.put(dat);
        buff.force();
        chan.close();
        ra.close();
        //unmap (buff);
        offset = offs;
        displayMap();
    }

    /**
     * Read bytes from file by using a separate FileChannel
     * @param offs Position where to begin reading
     * @param len number of bytes to read
     * @return byte Array containing requested file content
     * @throws Exception if something went wrong
     */
    public byte[] getBytes (long offs, int len) throws Exception
    {
        RandomAccessFile ra = new RandomAccessFile(inputFile, "rws");
        FileChannel chan = ra.getChannel();
        MappedByteBuffer buff = chan.map(FileChannel.MapMode.READ_WRITE, offs, len);
        byte[] ret = new byte[len];
        buff.get(ret);
        chan.close();
        ra.close();
        //unmap (buff);
        return ret;
    }


    /**
     * Draws mapped bytes into C64 display
     * @throws Exception if something went wrong
     */
    public void displayMap () throws Exception
    {
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, mappedLength);
        byteBuffer.get(mappedBytes);
        for (int s = 0; s < C64VideoMatrix.LINES_ON_SCREEN; s++)
        {
            C64Character[] c64 = matrix.get(s);
            String addr = format("%08x", s * 8 + offset);
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
        long pos = offset + s * 8 + t;
        return pos == filesize;
    }

    /**
     * Cursor hits bottom
     */
    public void scrollUp ()
    {
        offset += 8;
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
        offset -= 8;
        if (offset < 0)
            offset = 0;
        try
        {
            displayMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    public void setBytes (long address, byte[] data) throws Exception
//    {
//        System.arraycopy(data, 0, mappedBytes, (int) (address - offset), data.length);
//        byteBuffer.position(0);
//        byteBuffer.put(mappedBytes);
//        byteBuffer.force();
//        displayMap();
//    }

    /**
     * Scrollbar is moved
     * @param value Scrollbar value
     */
    public void setScrollbarOffset (long value)
    {
        offset = value;
        try
        {
            displayMap();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
