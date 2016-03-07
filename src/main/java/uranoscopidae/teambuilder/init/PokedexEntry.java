package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.TypeList;
import uranoscopidae.teambuilder.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.*;

public class PokedexEntry implements Comparable<PokedexEntry>
{
    private final int regionalDexID;
    private final int nationalID;
    private final Pokemon pokemon;
    private String description;
    private BufferedImage artwork;
    private BufferedImage icon;

    public PokedexEntry(int regionalDexID, int nationalID, Pokemon pokemon)
    {
        this.regionalDexID = regionalDexID;
        this.nationalID = nationalID;
        this.pokemon = pokemon;
        description = "Not fetched yet";

        artwork = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        artwork.setRGB(0,0,0xFF000000);

        icon = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        icon.setRGB(0,0,0xFF000000);
    }

    public Pokemon getPokemon()
    {
        return pokemon;
    }

    public int getNationalID()
    {
        return nationalID;
    }

    public int getRegionalDexID()
    {
        return regionalDexID;
    }

    @Override
    public int compareTo(PokedexEntry o)
    {
        return Integer.compare(getNationalID(), o.getNationalID());
    }

    @Override
    public String toString()
    {
        return "PokédexEntry[nationalID:"+nationalID+", regionalID:"+regionalDexID+", pokémon:"+pokemon.toString()+"]";
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
        System.out.println();
    }

    public void setArtwork(BufferedImage artwork)
    {
        this.artwork = artwork;
    }

    public BufferedImage getArtwork()
    {
        return artwork;
    }

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
        // TODO

        out.putNextEntry(new ZipEntry("globalInfos"));
        writeGlobalInfo(dataOut);

        dataOut.flush();
        out.flush();
    }

    private void writeMetadata(DataOutputStream out) throws IOException
    {
        out.writeInt(Constants.DEX_FORMAT_VERSION_NUMBER);
        out.flush();
    }

    private void writeGlobalInfo(DataOutputStream out) throws IOException
    {
        out.writeUTF(pokemon.getEnglishName());
        out.writeUTF(pokemon.getFirstType().getName());
        out.writeUTF(pokemon.getSecondType().getName());
        out.writeInt(nationalID);
        out.writeInt(regionalDexID);
        out.writeUTF(description);
        out.flush();
    }

    public static PokedexEntry readEntry(ZipInputStream in) throws IOException
    {
        ZipEntry entry;
        DataInputStream dataIn = new DataInputStream(in);
        BufferedImage artwork = null;
        BufferedImage icon = null;
        Pokemon pokemon = null;
        PokedexEntry dexEntry = null;
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
                    pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType));
                    dexEntry = new PokedexEntry(regionalID, nationalID, pokemon);
                    break;

                case "moves":
                    // TODO
                    break;

                default:
                    System.err.println("Unknown entry for Pokédex entry: " + entry.getName());
                    break;

            }
        }
        if(dexEntry != null)
        {
            if(artwork != null)
                dexEntry.setArtwork(artwork);
            dexEntry.setIcon(icon);
        }
        return dexEntry;
    }

    public void setIcon(BufferedImage icon)
    {
        this.icon = icon;
    }

    public BufferedImage getIcon()
    {
        return icon;
    }
}
