package main;

import terminal.C64Character;
import terminal.C64VideoMatrix;
import terminal.CharacterWriter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileMapper
{
    private FileChannel fileChannel;
    private int offset = 0;

    private C64VideoMatrix matrix;

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

    public void displayLines () throws Exception
    {
        final int len = 25*8;
        int start = 0;
        byte[] bytes = new byte[len];
        char[] chars = new char[len];
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, len);
        buffer.get(bytes);
        for (int s = 0; s<chars.length; s++)
        {
            char c =(char)bytes[s];
            if (Character.isLetterOrDigit(c))
                chars[s] = c;
            else
                chars[s] = '.';
        }
        for (int s = 0; s<25; s++)
        {
            StringBuilder sb = new StringBuilder();
            C64Character[] c64 = matrix.get(s);
            sb.append(String.format("%08x", start+offset)).append(' ');
            for (int t=0; t<8; t++)
            {
                sb.append (String.format("%02x", bytes[t+start])).append(' ');
            }
            for (int t=0; t<8; t++)
            {
                sb.append (String.format("%c", chars[t+start]));
            }
            start += 8;
            char[] cc = sb.toString().toCharArray();
            for (int t=0; t<c64.length; t++)
            {
                c64[t].face = CharacterWriter.getInstance().mapPCtoCBM(cc[t]);
                c64[t].colorIndex = 3;
            }
        }
    }
}
