package uranoscopidae.teambuilder.utils;

import java.io.IOException;
import java.io.OutputStream;

public class DirectByteArrayOutputSteam extends OutputStream
{
    private byte[] buf;
    private int index;

    public DirectByteArrayOutputSteam(byte[] buf)
    {
        this.buf = buf;
        index = 0;
    }

    @Override
    public void write(int b) throws IOException
    {
        buf[index++] = (byte) b;
    }

    public void setBuf(byte[] buf)
    {
        this.buf = buf;
        index = 0;
    }
}
