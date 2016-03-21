package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.PokedexExtractor;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
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

    private final List<Move> moves;
    private final List<Ability> abilities;
    private final int regionalDexID;

    private final int nationalDexID;
    private String description;
    private BufferedImage sprite;
    private BufferedImage shinySprite;
    private BufferedImage icon;
    private boolean spriteFetched;
    private boolean shinySpriteFetched;

    public Pokemon(String name, Type firstType, int regionalID, int nationalDexID)
    {
        this(name, firstType, TypeList.none, regionalID, nationalDexID);
    }

    public Pokemon(String name, Type firstType, Type secondType, int regionalID, int nationalDexID)
    {
        this(name, firstType, secondType, regionalID, nationalDexID, new LinkedList<>());
    }

    public Pokemon(String name, Type firstType, Type secondType, int regionalID, int nationalDexID, List<Move> moves)
    {
        this.regionalDexID = regionalID;
        this.nationalDexID = nationalDexID;
        this.name = name;
        this.firstType = firstType;
        this.secondType = secondType;
        this.moves = moves;

        abilities = new LinkedList<Ability>();

        description = "Not fetched yet";

        sprite = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        sprite.setRGB(0,0,0xFF000000);

        shinySprite = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        shinySprite.setRGB(0,0,0xFF000000);

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

    public void setSprite(BufferedImage sprite)
    {
        spriteFetched = true;
        this.sprite = sprite;
    }

    public BufferedImage getSprite()
    {
        return sprite;
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

    public List<Move> getMoves()
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
    public void writeTo(PokedexExtractor extractor, ZipOutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        out.putNextEntry(new ZipEntry("meta"));
        writeMetadata(dataOut);

        out.putNextEntry(new ZipEntry("sprite.png"));
        if(!spriteFetched)
        {
            if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6x")+".png"))
            {
                // try male form
                if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6x", "m")+".png"))
                {
                    // try unreleased
                    if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6o")+".png"))
                    {
                        extractor.getExtractor().outputImageTo(out, "File:Question_Mark.png");
                        // no other options...
                    }
                }
            }
            spriteFetched = true;
        }
        else
        {
            ImageIO.write(sprite, "png", out);
        }

        out.putNextEntry(new ZipEntry("shiny_sprite.png"));
        if(!shinySpriteFetched)
        {
            if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6x")+"_s.png"))
            {
                // try male form
                if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6x", "m")+"_s.png"))
                {
                    // try unreleased
                    if(!extractor.getExtractor().outputImageTo(out, extractor.getSpriteFullID(this, "6o")+"_s.png"))
                    {
                        extractor.getExtractor().outputImageTo(out, "File:Question_Mark.png");
                        // no other options...
                    }
                }
            }
            shinySpriteFetched = true;
        }
        else
        {
            ImageIO.write(shinySprite, "png", out);
        }

        out.putNextEntry(new ZipEntry("icon.png"));
        ImageIO.write(icon, "png", out);

        out.putNextEntry(new ZipEntry("moves"));
        dataOut.writeInt(getMoves().size());
        for(Move move : getMoves())
        {
            writeMove(dataOut, move);
        }

        out.putNextEntry(new ZipEntry("globalInfos"));
        writeGlobalInfo(dataOut);

        out.putNextEntry(new ZipEntry("abilities"));
        dataOut.writeInt(getAbilities().size());
        for(Ability ability : getAbilities())
        {
            writeAbility(dataOut, ability);
        }

        dataOut.flush();
        out.flush();
    }

    private void writeAbility(DataOutputStream dataOut, Ability ability) throws IOException
    {
        IOHelper.writeUTF(dataOut, ability.getEnglishName());
    }

    private void writeMove(DataOutputStream out, Move move) throws IOException
    {
        // write definition name, will be fetched later
        IOHelper.writeUTF(out, move.getEnglishName());
    }

    private void writeMetadata(DataOutputStream out) throws IOException
    {
        out.writeInt(Constants.DEX_FORMAT_VERSION_NUMBER);
        out.flush();
    }

    private void writeGlobalInfo(DataOutputStream out) throws IOException
    {
        IOHelper.writeUTF(out, getEnglishName());
        IOHelper.writeUTF(out, getFirstType().getName());
        IOHelper.writeUTF(out, getSecondType().getName());
        out.writeInt(nationalDexID);
        out.writeInt(regionalDexID);
        IOHelper.writeUTF(out, description);
        out.flush();
    }

    private static Move readMove(TeamBuilderApp app, DataInputStream dataIn) throws IOException, ReflectiveOperationException
    {
        String definition = IOHelper.readUTF(dataIn);
        return app.getMove(definition);
    }

    private static Ability readAbility(TeamBuilderApp app, DataInputStream dataIn) throws IOException
    {
        String name = IOHelper.readUTF(dataIn);
        return app.getAbility(name);
    }

    public static Pokemon readPokemon(TeamBuilderApp app, ZipInputStream in) throws IOException, ReflectiveOperationException
    {
        ZipEntry entry;
        DataInputStream dataIn = new DataInputStream(in);
        BufferedImage sprite = null;
        BufferedImage shinySprite = null;
        BufferedImage icon = null;
        Pokemon pokemon = null;
        List<Move> moves = new LinkedList<>();
        List<Ability> abilities = new LinkedList<>();
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

                case "sprite.png":
                    sprite = ImageIO.read(in);
                    break;

                case "shiny_sprite.png":
                    shinySprite = ImageIO.read(in);
                    break;

                case "icon.png":
                    icon = ImageIO.read(in);
                    break;

                case "globalInfos":
                    String name = IOHelper.readUTF(dataIn);
                    String firstType = IOHelper.readUTF(dataIn);
                    String secondType = IOHelper.readUTF(dataIn);
                    int nationalID = dataIn.readInt();
                    int regionalID = dataIn.readInt();
                    String desc = IOHelper.readUTF(dataIn);
                    pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType), regionalID, nationalID, moves);
                    pokemon.setDescription(desc);
                    break;

                case "moves":
                    int count = dataIn.readInt();
                    for (int i = 0; i < count; i++)
                    {
                        try
                        {
                            Move move = readMove(app, dataIn);
                            moves.add(move);
                        }
                        catch (UnsupportedOperationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "abilities":
                    int abilityCount = dataIn.readInt();
                    for (int i = 0; i < abilityCount; i++)
                    {
                        try
                        {
                            Ability ability = readAbility(app, dataIn);
                            abilities.add(ability);
                        }
                        catch (UnsupportedOperationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    System.err.println("Unknown entry for Pokédex entry: " + entry.getName());
                    break;

            }
        }
        if (pokemon != null)
        {
            pokemon.getAbilities().addAll(abilities);
            if (sprite != null)
                pokemon.setSprite(sprite);
            if (icon != null)
                pokemon.setIcon(icon);
            if (shinySprite != null)
                pokemon.setShinySprite(shinySprite);
        }
        return pokemon;
    }

    public List<Ability> getAbilities()
    {
        return abilities;
    }

    public void setShinySprite(BufferedImage shinySprite)
    {
        this.shinySprite = shinySprite;
    }

    public BufferedImage getShinySprite()
    {
        return shinySprite;
    }

    public String getFullID()
    {
        DecimalFormat format = new DecimalFormat("000");
        return format.format(getNationalDexID())+getEnglishName();
    }

    public boolean canLearn(Move move)
    {
        return moves.contains(move);
    }
}
