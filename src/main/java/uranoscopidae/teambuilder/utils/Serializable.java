package uranoscopidae.teambuilder.utils;

import java.io.*;

public interface Serializable
{

    void writeTo(DataOutputStream out) throws IOException;

    void readFrom(DataInputStream out) throws IOException;

}
