package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Pokemon implements Cloneable, Comparable<Pokemon>
{
    private final String name;
    private final Type firstType;
    private final Type secondType;
    private final LinkedList<Move> moves;

    private final int regionalDexID;
    private final int nationalDexID;

    private String description;
    private BufferedImage artwork;
    private BufferedImage icon;

    public Pokemon(String name, Type firstType, int regionalID, int nationalDexID)
    {
        this(name, firstType, TypeList.none, regionalID, nationalDexID);
    }

    public Pokemon(String name, Type firstType, Type secondType, int regionalID, int nationalDexID)
    {
        this.regionalDexID = regionalID;
        this.nationalDexID = nationalDexID;
        this.name = name;
        this.firstType = firstType;
        this.secondType = secondType;
        moves = new LinkedList<>();

        description = "Not fetched yet";

        artwork = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        artwork.setRGB(0,0,0xFF000000);

        icon = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        icon.setRGB(0,0,0xFF000000);
    }

    public int getNationalDexID()
    {
        return nationalDexID;
    }

    public int getRegionalDexID()
    {
        return regionalDexID;
    }

    public String getEnglishName()
    {
        return name;
    }

    public Type getFirstType()
    {
        return firstType;
    }

    public Type getSecondType()
    {
        return secondType;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void echo()
    {
        System.out.println(toString());
        System.out.println("Description: "+description);
        System.out.println("===Moves===");
        for(Move def : getMoves())
        {
            System.out.println(def.getEnglishName()+" ("+def.getType().getName()+"/"+def.getCategory().name()+")");
        }
    }

    public void setArtwork(BufferedImage artwork)
    {
        this.artwork = artwork;
    }

    public BufferedImage getArtwork()
    {
        return artwork;
    }


    public float calculateAttackedMultiplier(Type attackType)
    {
        return attackType.getAffinity(firstType) * attackType.getAffinity(secondType);
    }

    @Override
    public String toString()
    {
        return "Pokémon["+name+", "+firstType.getName()+"/"+secondType.getName()+", #"+nationalDexID+"]";
    }

    @Override
    protected Object clone()
    {
        Pokemon clone = new Pokemon(name, firstType, secondType, regionalDexID, nationalDexID);
        clone.getMoves().addAll(getMoves());
        return clone;
    }

    public void addMove(Move move)
    {
        moves.add(move);
    }

    public LinkedList<Move> getMoves()
    {
        return moves;
    }

    @Override
    public int compareTo(Pokemon o)
    {
        return Integer.compare(getNationalDexID(), o.getNationalDexID());
    }

    public void setIcon(BufferedImage icon)
    {
        this.icon = icon;
    }

    public BufferedImage getIcon()
    {
        return icon;
    }

    // I/O methods
    public void writeTo(ZipOutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        out.putNextEntry(new ZipEntry("meta"));
        writeMetadata(dataOut);

        out.putNextEntry(new ZipEntry("artwork"));
        ImageIO.write(artwork, "png", out);

        out.putNextEntry(new ZipEntry("icon"));
        ImageIO.write(icon, "png", out);

        out.putNextEntry(new ZipEntry("moves"));
        dataOut.writeInt(getMoves().size());
        for(Move move : getMoves())
        {
            writeMove(dataOut, move);
        }

        out.putNextEntry(new ZipEntry("globalInfos"));
        writeGlobalInfo(dataOut);

        dataOut.flush();
        out.flush();
    }

    private void writeMove(DataOutputStream out, Move move) throws IOException
    {
        // write definition name, will be fetched later
        out.writeUTF(move.getEnglishName());
    }

    private void writeMetadata(DataOutputStream out) throws IOException
    {
        out.writeInt(Constants.DEX_FORMAT_VERSION_NUMBER);
        out.flush();
    }

    private void writeGlobalInfo(DataOutputStream out) throws IOException
    {
        out.writeUTF(getEnglishName());
        out.writeUTF(getFirstType().getName());
        out.writeUTF(getSecondType().getName());
        out.writeInt(nationalDexID);
        out.writeInt(regionalDexID);
        out.writeUTF(description);
        out.flush();
    }

    private static Move readMove(TeamBuilderApp app, DataInputStream dataIn) throws IOException, ReflectiveOperationException
    {
        String definition = dataIn.readUTF();
        return app.getMove(definition);
    }

    public static Pokemon readPokemon(TeamBuilderApp app, ZipInputStream in) throws IOException, ReflectiveOperationException
    {
        ZipEntry entry;
        DataInputStream dataIn = new DataInputStream(in);
        BufferedImage artwork = null;
        BufferedImage icon = null;
        Pokemon pokemon = null;
        List<Move> moves = new LinkedList<>();
        while ((entry = in.getNextEntry()) != null)
        {
            switch (entry.getName())
            {
                case "meta":
                    int version = dataIn.readInt();
                    if (version != Constants.DEX_FORMAT_VERSION_NUMBER)
                    {
                        throw new IOException("Version numbers do not match: found: " + version + " and current: " + Constants.DEX_FORMAT_VERSION_NUMBER);
                    }
                    break;

                case "artwork":
                    artwork = ImageIO.read(in);
                    break;

                case "icon":
                    icon = ImageIO.read(in);
                    break;

                case "globalInfos":
                    String name = dataIn.readUTF();
                    String firstType = dataIn.readUTF();
                    String secondType = dataIn.readUTF();
                    int nationalID = dataIn.readInt();
                    int regionalID = dataIn.readInt();
                    String desc = dataIn.readUTF();
                    pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType), regionalID, nationalID);
                    pokemon.setDescription(desc);
                    break;

                case "moves":
                    int count = dataIn.readInt();
                    for(int i = 0;i<count;i++)
                    {
                        Move move = readMove(app, dataIn);
                        moves.add(move);
                    }
                    break;

                default:
                    System.err.println("Unknown entry for Pokédex entry: " + entry.getName());
                    break;

            }
        }
        if(pokemon != null)
        {
            if(artwork != null)
                pokemon.setArtwork(artwork);
            if(icon != null)
                pokemon.setIcon(icon);
            pokemon.getMoves().addAll(moves);
        }
        return pokemon;
    }

}
