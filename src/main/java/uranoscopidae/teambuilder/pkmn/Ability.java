package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import java.io.*;

public class Ability
{

    private final String name;
    private final String desc;

    public Ability(String name, String desc)
    {
        this.name = name;
        this.desc = desc;
    }

    public String getEnglishName()
    {
        return name;
    }

    public String getDesc()
    {
        return desc;
    }

    public void writeTo(OutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(Constants.ABILITY_FORMAT_VERSION_NUMBER);
        IOHelper.writeUTF(dataOut, name);
        IOHelper.writeUTF(dataOut, desc);
        dataOut.flush();
    }

    public static Ability readFrom(InputStream in) throws IOException
    {
        DataInputStream dataIn = new DataInputStream(in);
        int version = dataIn.readInt();
        if(version != Constants.ABILITY_FORMAT_VERSION_NUMBER)
        {
            throw new UnsupportedOperationException("Format versions do not match (current: "+Constants.ABILITY_FORMAT_VERSION_NUMBER+", found: "+version+")");
        }
        String name = IOHelper.readUTF(dataIn);
        String desc = IOHelper.readUTF(dataIn);
        return new Ability(name, desc);
    }
}
