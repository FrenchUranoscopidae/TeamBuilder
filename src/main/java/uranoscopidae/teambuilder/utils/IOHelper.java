package uranoscopidae.teambuilder.utils;

import java.io.*;
import java.nio.charset.Charset;

public class IOHelper
{

    public static void writeUTF(DataOutputStream out, String text) throws IOException
    {
        byte[] bytes = text.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.write(bytes);
        out.writeChar('\0');
    }

    public static String readUTF(DataInputStream in) throws IOException
    {
        int length = in.readInt();
        byte[] chars = new byte[length];
        for (int i = 0; i < length;)
        {
            i += in.read(chars, i, chars.length-i);
        }
        char end = in.readChar();
        if(end != '\0')
        {
            throw new IOException("String did not end with null character");
        }
        return new String(chars, "UTF-8");
    }

    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buf = new byte[1024 * 16];
        int i;
        while((i = in.read(buf)) != -1)
        {
            out.write(buf, 0, i);
        }
        out.flush();
    }
}
