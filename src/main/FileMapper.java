package main;

import c64terminal.C64Character;
import c64terminal.C64Colors;
import c64terminal.C64VideoMatrix;
import c64terminal.CharacterWriter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.String.format;

public class FileMapper
{
    private FileChannel fileChannel;
    private MappedByteBuffer byteBuffer;
    private int offset = 0;
    private C64VideoMatrix matrix;

    final int len = C64VideoMatrix.LINES_ON_SCREEN*8;
    final byte[] bytes = new byte[len];

    public FileMapper (File f,C64VideoMatrix matrix) throws Exception
    {
        this.matrix = matrix;
        fileChannel = new RandomAccessFile(f, "rw").getChannel();
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
        System.arraycopy(data,0, bytes,(int)address-offset,data.length);
        //byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, len);
        byteBuffer.position(0);
        byteBuffer.put (bytes);
        byteBuffer.force();
        displayLines();
    }

    public void displayLines() throws Exception
    {
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, len);
        byteBuffer.get (bytes);
        int start = 0;
        for (int s = 0; s<C64VideoMatrix.LINES_ON_SCREEN; s++)
        {
            StringBuilder sb = new StringBuilder();
            C64Character[] c64 = matrix.get(s);
            String addr = format("%08x", start+offset);
            for (int t=0; t<8; t++)
            {
                CharacterWriter.getInstance().writeChar(c64[t],addr.charAt(t), C64Colors.YELLOW);
                int idx = 3*t+9;
                String temp = String.format("%02x", bytes[t+start]);
                CharacterWriter.getInstance().writeChar(c64[idx],temp.charAt(0), C64Colors.WHITE);
                CharacterWriter.getInstance().writeChar(c64[idx+1],temp.charAt(1),C64Colors.WHITE);
                idx = 33+t;
                CharacterWriter.getInstance().writeChar(c64[idx],(char)bytes[t+start], C64Colors.PURPLE);
            }
            start += 8;
        }
    }
}
