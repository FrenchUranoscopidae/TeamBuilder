package uranoscopidae.teambuilder.pkmn.moves;

import uranoscopidae.teambuilder.pkmn.Type;
import uranoscopidae.teambuilder.pkmn.TypeList;
import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import java.io.*;

public class MoveInfos
{
    private final int pokeApiID;
    private final Type type;
    private final MoveCategory category;
    private final String englishName;

    private final int power;
    private final int accuracy;
    private final int powerPoints;
    private String description = "<MISSINGDESC>";

    public MoveInfos(int pokeApiID, Type type, MoveCategory category, String englishName, int power, int accuracy, int powerPoints)
    {
        this.pokeApiID = pokeApiID;
        this.type = type;
        this.category = category;
        this.englishName = englishName;
        this.power = power;
        this.accuracy = accuracy;
        this.powerPoints = powerPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPokeApiID() {
        return pokeApiID;
    }

    public int getAccuracy()
    {
        return accuracy;
    }

    public MoveCategory getCategory()
    {
        return category;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public int getPower()
    {
        return power;
    }

    public int getPowerPoints()
    {
        return powerPoints;
    }

    public Type getType()
    {
        return type;
    }

    public void writeTo(OutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(Constants.MOVE_FORMAT_VERSION_NUMBER);
        dataOut.writeInt(getPokeApiID());
        IOHelper.writeUTF(dataOut, type.getName());
        IOHelper.writeUTF(dataOut, category.name().toUpperCase());
        IOHelper.writeUTF(dataOut, englishName);
        dataOut.writeInt(power);
        dataOut.writeInt(accuracy);
        dataOut.writeInt(powerPoints);
        dataOut.flush();
    }

    public static MoveInfos readFrom(InputStream in) throws IOException
    {
        DataInputStream dataIn = new DataInputStream(in);
        int version = dataIn.readInt();
        if(version != Constants.MOVE_FORMAT_VERSION_NUMBER)
        {
            throw new UnsupportedOperationException("Format versions do not match (current: "+Constants.MOVE_FORMAT_VERSION_NUMBER+", found: "+version+")");
        }
        String type = IOHelper.readUTF(dataIn);
        String category = IOHelper.readUTF(dataIn);
        String name = IOHelper.readUTF(dataIn);
        int id = dataIn.readInt();
        int power = dataIn.readInt();
        int accuracy = dataIn.readInt();
        int pp = dataIn.readInt();
        return new MoveInfos(id, TypeList.getFromID(type), MoveCategory.valueOf(category), name, power, accuracy, pp);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof MoveInfos)
        {
            return ((MoveInfos) obj).getEnglishName().equals(getEnglishName());
        }
        return super.equals(obj);
    }
}
