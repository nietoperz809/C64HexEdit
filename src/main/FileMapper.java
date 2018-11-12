package main;

import c64terminal.C64Character;
import c64terminal.C64Color;
import c64terminal.C64VideoMatrix;
import c64terminal.CharacterWriter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.String.format;

public class FileMapper
{
    private static FileChannel fileChannel;
    private static RandomAccessFile raFile;

    private MappedByteBuffer byteBuffer;
    private long  offset = 0;
    private long filesize = 0;
    private final C64VideoMatrix matrix;
    private File inputFile;

    private final int mappedLength = C64VideoMatrix.LINES_ON_SCREEN*8;
    private final byte[] mappedBytes = new byte[mappedLength];

    public FileMapper (File f, C64VideoMatrix matrix) throws Exception
    {
        close();
        this.matrix = matrix;
        this.inputFile = f;
        filesize = f.length();
        open();
    }

    public void open() throws Exception
    {
        raFile = new RandomAccessFile(inputFile, "rws");
        fileChannel = raFile.getChannel();
    }

    public void close() throws Exception
    {
        if (fileChannel != null)
            fileChannel.close();
        if (raFile != null)
            raFile.close();
    }

    public void putBytes (byte[] dat, long offs) throws Exception
    {
        RandomAccessFile ra = new RandomAccessFile(inputFile, "rws");
        FileChannel chan = ra.getChannel();
        MappedByteBuffer buff = chan.map(FileChannel.MapMode.READ_WRITE, offs, dat.length);
        buff.put(dat);
        buff.force();
        chan.close();
        ra.close();
        offset = offs;
        displayLines();
    }

    public void scrollUp()
    {
        offset += 8;
        try
        {
            displayLines();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void scrollDown()
    {
        if (offset != 0)
        {
            offset -= 8;
            try
            {
                displayLines();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setBytes (long address, byte[] data) throws Exception
    {
        System.arraycopy(data,0, mappedBytes,(int)(address-offset),data.length);
        //byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, mappedLength);
        byteBuffer.position(0);
        byteBuffer.put (mappedBytes);
        byteBuffer.force();
        displayLines();
    }

    private boolean isPosFileEnd (int t, int s) throws Exception
    {
        long pos = offset+s*8+t;
        return pos == filesize;
    }

    public void displayLines() throws Exception
    {
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, mappedLength);
        byteBuffer.get (mappedBytes);
        for (int s = 0; s<C64VideoMatrix.LINES_ON_SCREEN; s++)
        {
            C64Character[] c64 = matrix.get(s);
            String addr = format("%08x", s*8+offset);
            CharacterWriter cw = CharacterWriter.getInstance();
            for (int t=0; t<8; t++)
            {
                cw.writeChar(c64[t],addr.charAt(t), C64Color.YELLOW);
                int idx = 3*t+9;
                int src_idx = t+s*8;
                String temp = String.format("%02x", mappedBytes[src_idx]);
                C64Color col = isPosFileEnd(t,s) ? C64Color.BLACK : C64Color.WHITE;
                cw.writeChar(c64[idx], temp.charAt(0), col);
                cw.writeChar(c64[idx + 1], temp.charAt(1), col);
                idx = 33+t;
                cw.writeChar(c64[idx],(char) mappedBytes[src_idx], C64Color.ORANGE);
            }
        }
    }

    public void setScrollbarOffset (long value)
    {
        offset = value;
        try
        {
            displayLines();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
